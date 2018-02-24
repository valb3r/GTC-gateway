package com.gtc.tradinggateway.controller;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import com.gtc.model.tradinggateway.api.dto.command.create.CreateOrderCommand;
import com.gtc.model.tradinggateway.api.dto.command.manage.CancelOrderCommand;
import com.gtc.model.tradinggateway.api.dto.command.manage.GetOrderCommand;
import com.gtc.model.tradinggateway.api.dto.command.manage.ListOpenCommand;
import com.gtc.model.tradinggateway.api.dto.command.withdraw.WithdrawCommand;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.model.tradinggateway.api.dto.response.create.CreateOrderResponse;
import com.gtc.model.tradinggateway.api.dto.response.manage.CancelOrderResponse;
import com.gtc.model.tradinggateway.api.dto.response.manage.GetOrderResponse;
import com.gtc.model.tradinggateway.api.dto.response.manage.ListOpenOrdersResponse;
import com.gtc.model.tradinggateway.api.dto.response.withdraw.WithdrawOrderResponse;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.ClientNamed;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by Valentyn Berezin on 24.02.18.
 */
@Slf4j
@RestController
@ConditionalOnProperty(name = "REST_ENABLED", havingValue = "true")
@RequiredArgsConstructor
public class RestCommandHandler {

    private final Map<String, CreateOrder> createOps;
    private final Map<String, ManageOrders> manageOps;
    private final Map<String, Withdraw> withdrawOps;

    @PostMapping("create")
    public AbstractMessage create(@Valid CreateOrderCommand command) {
        log.info("Request to create order {}", command);
        return doExecute(command, createOps, (handler, cmd) -> {
            String id = handler.create(
                    TradingCurrency.fromCode(cmd.getCurrencyFrom()),
                    TradingCurrency.fromCode(cmd.getCurrencyTo()),
                    cmd.getAmount().doubleValue(),
                    cmd.getPrice().doubleValue()
            );

            log.info("Created {} for {} of {}", id, cmd.getId(), cmd.getClientName());
            return CreateOrderResponse.builder()
                    .clientName(cmd.getClientName())
                    .id(cmd.getId())
                    .orderId(id)
                    .build();
        });
    }

    @PostMapping("get")
    public AbstractMessage get(@Valid GetOrderCommand command) {
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
    public AbstractMessage listOpen(@Valid ListOpenCommand command) {
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
    public AbstractMessage cancel(@Valid CancelOrderCommand command) {
        log.info("Request to cancel order {}", command);
        return doExecute(command, manageOps, (handler, cmd) -> {
            handler.cancel(cmd.getOrderId());

            log.info("Cancelled order {} for {}", cmd.getOrderId(), cmd.getClientName());
            return CancelOrderResponse.builder()
                    .clientName(cmd.getClientName())
                    .id(cmd.getId())
                    .build();
        });
    }

    @PostMapping("withdraw")
    public AbstractMessage withdraw(@Valid WithdrawCommand command) {
        log.info("Request to withdraw {}", command);
        return doExecute(command, withdrawOps, (handler, cmd) -> {
            handler.withdraw(
                    TradingCurrency.fromCode(cmd.getCurrency()),
                    cmd.getAmount().doubleValue(),
                    cmd.getToDestination()
            );

            log.info("Withdraw {} by {} to {}", cmd.getCurrency(), cmd.getAmount(), cmd.getToDestination());
            return WithdrawOrderResponse.builder()
                    .clientName(cmd.getClientName())
                    .id(cmd.getId())
                    .currency(cmd.getCurrency())
                    .amount(cmd.getAmount().doubleValue())
                    .toDestination(cmd.getToDestination())
                    .build();
        });
    }

    private <T extends ClientNamed, U extends AbstractMessage> AbstractMessage doExecute(
            U message,
            Map<String, T> handlers,
            BiFunction<T, U, ? extends AbstractMessage> executor) {
        T handler = handlers.get(message.getClientName());
        return executor.apply(handler, message);
    }
}
