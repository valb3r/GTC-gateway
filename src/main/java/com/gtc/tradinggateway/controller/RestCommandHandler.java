package com.gtc.tradinggateway.controller;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import com.gtc.model.tradinggateway.api.dto.command.account.GetAllBalancesCommand;
import com.gtc.model.tradinggateway.api.dto.command.create.CreateOrderCommand;
import com.gtc.model.tradinggateway.api.dto.command.manage.CancelOrderCommand;
import com.gtc.model.tradinggateway.api.dto.command.manage.GetOrderCommand;
import com.gtc.model.tradinggateway.api.dto.command.manage.ListOpenCommand;
import com.gtc.model.tradinggateway.api.dto.command.withdraw.WithdrawCommand;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.model.tradinggateway.api.dto.response.account.GetAllBalancesResponse;
import com.gtc.model.tradinggateway.api.dto.response.create.CreateOrderResponse;
import com.gtc.model.tradinggateway.api.dto.response.manage.CancelOrderResponse;
import com.gtc.model.tradinggateway.api.dto.response.manage.GetOrderResponse;
import com.gtc.model.tradinggateway.api.dto.response.manage.ListOpenOrdersResponse;
import com.gtc.model.tradinggateway.api.dto.response.withdraw.WithdrawOrderResponse;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.*;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * FIXME: Kill duplicate code - bodies similar to EsbCommnadHandler
 * Created by Valentyn Berezin on 24.02.18.
 */
@Slf4j
@RestController
@ConditionalOnProperty(name = "REST_ENABLED", havingValue = "true")
public class RestCommandHandler {

    private final Map<String, Account> accountOps;
    private final Map<String, CreateOrder> createOps;
    private final Map<String, ManageOrders> manageOps;
    private final Map<String, Withdraw> withdrawOps;

    public RestCommandHandler(List<Account> accountCmds, List<CreateOrder> createCmds,
                              List<ManageOrders> manageCmds, List<Withdraw> withdrawCmds) {
        accountOps = accountCmds.stream().collect(Collectors.toMap(ClientNamed::name, it -> it));
        createOps = createCmds.stream().collect(Collectors.toMap(ClientNamed::name, it -> it));
        manageOps = manageCmds.stream().collect(Collectors.toMap(ClientNamed::name, it -> it));
        withdrawOps = withdrawCmds.stream().collect(Collectors.toMap(ClientNamed::name, it -> it));
    }

    @PostMapping("getBalances")
    public AbstractMessage getBalances(@RequestBody @Valid GetAllBalancesCommand command) {
        log.info("Request to create order {}", command);
        return doExecute(command, accountOps, (handler, cmd) -> {
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

    @PostMapping("create")
    public AbstractMessage create(@RequestBody @Valid CreateOrderCommand command) {
        log.info("Request to create order {}", command);
        return doExecute(command, createOps, (handler, cmd) -> {
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

    @PostMapping("get")
    public AbstractMessage get(@RequestBody @Valid GetOrderCommand command) {
        log.info("Request to get order {}", command);
        return doExecute(command, manageOps, (handler, cmd) -> {
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

    @PostMapping("list")
    public AbstractMessage listOpen(@RequestBody @Valid ListOpenCommand command) {
        log.info("Request to list orders {}", command);
        return doExecute(command, manageOps, (handler, cmd) -> {
            List<OrderDto> res = handler.getOpen();

            log.info("Found open orders {} for {}", res, cmd.getClientName());
            return ListOpenOrdersResponse.builder()
                    .clientName(cmd.getClientName())
                    .id(cmd.getId())
                    .orders(res)
                    .build();
        });
    }

    @PostMapping("cancel")
    public AbstractMessage cancel(@RequestBody @Valid CancelOrderCommand command) {
        log.info("Request to cancel order {}", command);
        return doExecute(command, manageOps, (handler, cmd) -> {
            handler.cancel(cmd.getOrderId());

            log.info("Cancelled order {} for {}", cmd.getOrderId(), cmd.getClientName());
            return CancelOrderResponse.builder()
                    .clientName(cmd.getClientName())
                    .id(cmd.getId())
                    .orderId(cmd.getOrderId())
                    .build();
        });
    }

    @PostMapping("withdraw")
    public AbstractMessage withdraw(@RequestBody @Valid WithdrawCommand command) {
        log.info("Request to withdraw {}", command);
        return doExecute(command, withdrawOps, (handler, cmd) -> {
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

    private <T extends ClientNamed, U extends AbstractMessage> AbstractMessage doExecute(
            U message,
            Map<String, T> handlers,
            BiFunction<T, U, ? extends AbstractMessage> executor) {
        T handler = handlers.get(message.getClientName());

        if (null == handler) {
            throw new IllegalStateException("No handler");
        }

        return executor.apply(handler, message);
    }
}
