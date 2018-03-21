package com.gtc.tradinggateway.service.bitfinex;

import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.tradinggateway.config.BitfinexConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.bitfinex.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.gtc.tradinggateway.config.Const.Clients.BITFINEX;

/**
 * Created by mikro on 15.02.2018.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BitfinexRestClient implements Withdraw, ManageOrders, Account {

    private static String SELL = "sell";
    private static String ORDERS = "/v1/orders";
    private static String ORDER = "/v1/order/status";
    private static String ORDER_CANCEL = "/v1/order/cancel";
    private static String WITHDRAW = "/v1/withdraw";
    private static String BALANCE = "/v1/balances";

    private static String EXCHANGE_TYPE = "exchange";

    private final BitfinexConfig cfg;
    private final BitfinexEncryptionService signer;

    public Optional<OrderDto> get(String id) {
        BitfinexGetOrderRequestDto requestDto = new BitfinexGetOrderRequestDto(ORDER, Long.valueOf(id));
        ResponseEntity<BitfinexOrderDto> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDER,
                        HttpMethod.POST,
                        new HttpEntity<>(signer.restHeaders(requestDto)),
                        BitfinexOrderDto.class);
        return Optional.of(parseOrderDto(resp.getBody()));
    }

    public List<OrderDto> getOpen() {
        BitfinexRequestDto requestDto = new BitfinexRequestDto(ORDERS);
        ResponseEntity<BitfinexOrderDto[]> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDERS,
                        HttpMethod.POST,
                        new HttpEntity<>(signer.restHeaders(requestDto)),
                        BitfinexOrderDto[].class);
        BitfinexOrderDto[] orders = resp.getBody();
        List<OrderDto> result = new ArrayList<>();
        for (BitfinexOrderDto order : orders) {
            result.add(parseOrderDto(order));
        }
        return result;
    }

    public void cancel(String id) {
        BitfinexGetOrderRequestDto requestDto = new BitfinexGetOrderRequestDto(ORDER_CANCEL, Long.valueOf(id));
        cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDER_CANCEL,
                        HttpMethod.POST,
                        new HttpEntity<>(signer.restHeaders(requestDto)),
                        Object.class);
    }

    public void withdraw(TradingCurrency currency, BigDecimal amount, String destination) {
        BitfinexWithdrawRequestDto requestDto =
                new BitfinexWithdrawRequestDto(WITHDRAW, cfg.getSymbols().get(currency), amount, destination);
        cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + WITHDRAW,
                        HttpMethod.POST,
                        new HttpEntity<>(signer.restHeaders(requestDto)),
                        Object.class);
    }

    public Map<TradingCurrency, BigDecimal> balances() {
        BitfinexRequestDto requestDto = new BitfinexRequestDto(BALANCE);
        ResponseEntity<BitfinexBalanceItemDto[]> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + BALANCE,
                        HttpMethod.POST,
                        new HttpEntity<>(signer.restHeaders(requestDto)),
                        BitfinexBalanceItemDto[].class);

        Map<TradingCurrency, BigDecimal> results = new EnumMap<>(TradingCurrency.class);
        BitfinexBalanceItemDto[] assets = resp.getBody();
        for (BitfinexBalanceItemDto asset : assets) {
            if (!EXCHANGE_TYPE.equals(asset.getType())) {
                continue;
            }
            try {
                results.put(TradingCurrency.fromCode(asset.getCurrency()), asset.getAmount());
            } catch (RuntimeException ex) {
                log.error(
                        "Failed mapping currency-code {} having amount {}",
                        asset.getCurrency().toString(), String.valueOf(asset.getAmount()));
            }
        }
        return results;
    }

    private OrderDto parseOrderDto(BitfinexOrderDto response) {
        return OrderDto.builder()
                .orderId(response.getId())
                .size(SELL.equals(response.getSide())
                        ? response.getAmount().multiply(new BigDecimal(-1))
                        : response.getAmount())
                .price(response.getPrice())
                .status(response.getStatus())
                .build();
    }

    @Override
    public String name() {
        return BITFINEX;
    }
}
