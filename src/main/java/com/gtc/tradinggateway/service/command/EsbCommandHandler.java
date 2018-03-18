package com.gtc.tradinggateway.service.command;

import com.google.common.base.Throwables;
import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import com.gtc.model.tradinggateway.api.dto.WithOrderId;
import com.gtc.model.tradinggateway.api.dto.command.account.GetAllBalancesCommand;
import com.gtc.model.tradinggateway.api.dto.command.create.CreateOrderCommand;
import com.gtc.model.tradinggateway.api.dto.command.manage.CancelOrderCommand;
import com.gtc.model.tradinggateway.api.dto.command.manage.GetOrderCommand;
import com.gtc.model.tradinggateway.api.dto.command.manage.ListOpenCommand;
import com.gtc.model.tradinggateway.api.dto.command.withdraw.WithdrawCommand;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.model.tradinggateway.api.dto.response.ErrorResponse;
import com.gtc.model.tradinggateway.api.dto.response.account.GetAllBalancesResponse;
import com.gtc.model.tradinggateway.api.dto.response.create.CreateOrderResponse;
import com.gtc.model.tradinggateway.api.dto.response.manage.CancelOrderResponse;
import com.gtc.model.tradinggateway.api.dto.response.manage.GetOrderResponse;
import com.gtc.model.tradinggateway.api.dto.response.manage.ListOpenOrdersResponse;
import com.gtc.model.tradinggateway.api.dto.response.withdraw.WithdrawOrderResponse;
import com.gtc.tradinggateway.aspect.rate.RateTooHighException;
import com.gtc.tradinggateway.config.ClientsConf;
import com.gtc.tradinggateway.config.JmsConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.*;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by Valentyn Berezin on 20.02.18.
 */
@Slf4j
@Service
@Async
@ConditionalOnBean(JmsConfig.class)
public class EsbCommandHandler {

    private static final String ACCOUNT_TOPIC = "${app.jms.topic.inOut.account}";
    private static final String CREATE_TOPIC = "${app.jms.topic.inOut.create}";
    private static final String MANAGE_TOPIC = "${app.jms.topic.inOut.manage}";
    private static final String WITHDRAW_TOPIC = "${app.jms.topic.inOut.withdraw}";

    @Value(ACCOUNT_TOPIC)
    private String accountTopic;

    @Value(CREATE_TOPIC)
    private String createTopic;

    @Value(MANAGE_TOPIC)
    private String manageTopic;

    @Value(WITHDRAW_TOPIC)
    private String withdrawTopic;

    private final JmsTemplate jmsTemplate;
    private final Map<String, Account> accountOps;
    private final Map<String, CreateOrder> createOps;
    private final Map<String, ManageOrders> manageOps;
    private final Map<String, Withdraw> withdrawOps;

    public EsbCommandHandler(ClientsConf conf, JmsTemplate jmsTemplate, List<Account> accountCmds,
                               List<CreateOrder> createCmds, List<ManageOrders> manageCmds,
                               List<Withdraw> withdrawCmds) {
        this.jmsTemplate = jmsTemplate;
        accountOps = accountCmds.stream().filter(it -> conf.getActive().contains(it.name()))
                .collect(Collectors.toMap(ClientNamed::name, it -> it));
        createOps = createCmds.stream().filter(it -> conf.getActive().contains(it.name()))
                .collect(Collectors.toMap(ClientNamed::name, it -> it));
        manageOps = manageCmds.stream().filter(it -> conf.getActive().contains(it.name()))
                .collect(Collectors.toMap(ClientNamed::name, it -> it));
        withdrawOps = withdrawCmds.stream().filter(it -> conf.getActive().contains(it.name()))
                .collect(Collectors.toMap(ClientNamed::name, it -> it));
    }

    @Trace(dispatcher = true)
    @JmsListener(destination = ACCOUNT_TOPIC, selector = GetAllBalancesCommand.SELECTOR)
    public void getAllBalances(@Valid GetAllBalancesCommand command) {
        log.info("Request to get balances {}", command);
        doExecute(accountTopic, command, accountOps, (handler, cmd) -> {
            Map<TradingCurrency, BigDecimal> balances = handler.balances();

            log.info("Got balances {} for {} of {}", balances, cmd.getId(), cmd.getClientName());
            return GetAllBalancesResponse.builder()
                    .clientName(cmd.getClientName())
                    .id(cmd.getId())
                    .balances(balances.entrySet().stream()
                            .collect(Collectors.toMap(it -> it.getKey().getCode(), Map.Entry::getValue))
                    ).build();
        });
    }

    @Trace(dispatcher = true)
    @JmsListener(destination = CREATE_TOPIC, selector = CreateOrderCommand.SELECTOR)
    public void create(@Valid CreateOrderCommand command) {
        log.info("Request to create order {}", command);
        doExecute(createTopic, command, createOps, (handler, cmd) -> {
            Optional<OrderCreatedDto> res = handler.create(
                    cmd.getOrderId(),
                    TradingCurrency.fromCode(cmd.getCurrencyFrom()),
                    TradingCurrency.fromCode(cmd.getCurrencyTo()),
                    cmd.getAmount(),
                    cmd.getPrice()
            );

            log.info("Created {} for {} of {}", res, cmd.getId(), cmd.getClientName());
            return res.map(id -> CreateOrderResponse.builder()
                    .clientName(cmd.getClientName())
                    .id(cmd.getId())
                    .requestOrderId(cmd.getId())
                    .orderId(id.getAssignedId())
                    .isExecuted(id.isExecuted())
                    .build()
            ).orElse(null);
        });
    }

    @Trace(dispatcher = true)
    @JmsListener(destination = MANAGE_TOPIC, selector = GetOrderCommand.SELECTOR)
    public void get(@Valid GetOrderCommand command) {
        log.info("Request to get order {}", command);
        doExecute(manageTopic, command, manageOps, (handler, cmd) -> {
            OrderDto res = handler.get(
                    cmd.getOrderId()
            ).orElse(null);

            log.info("Found {} for {} of {}", res, cmd.getOrderId(), cmd.getClientName());
            return GetOrderResponse.builder()
                    .clientName(cmd.getClientName())
                    .id(cmd.getId())
                    .order(res)
                    .build();
        });
    }

    @Trace(dispatcher = true)
    @JmsListener(destination = MANAGE_TOPIC, selector = ListOpenCommand.SELECTOR)
    public void listOpen(@Valid ListOpenCommand command) {
        log.info("Request to list orders {}", command);
        doExecute(manageTopic, command, manageOps, (handler, cmd) -> {
            List<OrderDto> res = handler.getOpen();

            log.info("Found open orders {} for {}", res, cmd.getClientName());
            return ListOpenOrdersResponse.builder()
                    .clientName(cmd.getClientName())
                    .id(cmd.getId())
                    .orders(res)
                    .build();
        });
    }

    @Trace(dispatcher = true)
    @JmsListener(destination = MANAGE_TOPIC, selector = CancelOrderCommand.SELECTOR)
    public void cancel(@Valid CancelOrderCommand command) {
        log.info("Request to cancel order {}", command);
        doExecute(manageTopic, command, manageOps, (handler, cmd) -> {
            handler.cancel(cmd.getOrderId());

            log.info("Cancelled order {} for {}", cmd.getOrderId(), cmd.getClientName());
            return CancelOrderResponse.builder()
                    .clientName(cmd.getClientName())
                    .id(cmd.getId())
                    .orderId(command.getOrderId())
                    .build();
        });
    }

    @Trace(dispatcher = true)
    @JmsListener(destination = WITHDRAW_TOPIC, selector = WithdrawCommand.SELECTOR)
    public void withdraw(@Valid WithdrawCommand command) {
        log.info("Request to withdraw {}", command);
        doExecute(withdrawTopic, command, withdrawOps, (handler, cmd) -> {
            handler.withdraw(
                    TradingCurrency.fromCode(cmd.getCurrency()),
                    cmd.getAmount(),
                    cmd.getToDestination()
            );

            log.info("Withdraw {} by {} to {}", cmd.getCurrency(), cmd.getAmount(), cmd.getToDestination());
            return WithdrawOrderResponse.builder()
                    .clientName(cmd.getClientName())
                    .id(cmd.getId())
                    .currency(cmd.getCurrency())
                    .amount(cmd.getAmount())
                    .toDestination(cmd.getToDestination())
                    .build();
        });
    }

    private <T extends ClientNamed, U extends AbstractMessage> void doExecute(
            String dest,
            U message,
            Map<String, T> handlers,
            BiFunction<T, U, ? extends AbstractMessage> executor) {
        T handler = handlers.get(message.getClientName());

        if (null == handler) {
            log.warn("Missing handler for {}", message);
            ErrorResponse error = buildError(message, new NoClientException());
            jmsTemplate.convertAndSend(dest, error, error::enhance);
            return;
        }

        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(message.getClientName() + " / " + message.getId());

        try {
            AbstractMessage result = executor.apply(handler, message);
            // result can be null if it was WS based request
            if (null != result) {
                jmsTemplate.convertAndSend(dest, result, result::enhance);
            }
        } catch (RateTooHighException ex) {
            NewRelic.noticeError(ex);
            ErrorResponse error = buildError(message, ex);
            error.setTransient(true);
            log.error("Sending transient error message {} in response to {}", error, message.getId());
            jmsTemplate.convertAndSend(dest, error, error::enhance);
        } catch (Exception ex) {
            NewRelic.noticeError(ex);
            ErrorResponse error = buildError(message, ex);
            log.error("Sending error message {} in response to {}", error, message.getId());
            jmsTemplate.convertAndSend(dest, error, error::enhance);
        } finally {
            Thread.currentThread().setName(oldName);
        }
    }

    private static ErrorResponse buildError(AbstractMessage origin, Throwable forExc) {
        ErrorResponse resp = new ErrorResponse();
        resp.setClientName(origin.getClientName());
        resp.setId(UUID.randomUUID().toString());
        resp.setOnMessageId(origin.getId());
        resp.setOccurredOn(origin.toString());
        resp.setErrorCause(Throwables.getStackTraceAsString(Throwables.getRootCause(forExc)));

        if (origin instanceof WithOrderId) {
            resp.setOrderId(((WithOrderId) origin).getOrderId());
        }

        return resp;
    }

    private static class NoClientException extends IllegalStateException {
    }
}
