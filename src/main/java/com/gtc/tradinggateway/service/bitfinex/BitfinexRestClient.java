package com.gtc.tradinggateway.service.bitfinex;

import com.gtc.tradinggateway.config.BitfinexConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CancelOrder;
import com.gtc.tradinggateway.service.GetOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.bitfinex.dto.BitfinexGetOrderRequestDto;
import com.gtc.tradinggateway.service.bitfinex.dto.BitfinexOrderDto;
import com.gtc.tradinggateway.service.bitfinex.dto.BitfinexRequestDto;
import com.gtc.tradinggateway.service.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.gtc.tradinggateway.config.Const.Clients.BITFINEX;

/**
 * Created by mikro on 15.02.2018.
 */
@EnableScheduling
@Slf4j
@Service
@RequiredArgsConstructor
public class BitfinexRestClient implements GetOrders, Withdraw, CancelOrder, Account {

    private static String SELL = "sell";
    private static String ORDERS = "/v1/orders";
    private static String ORDER = "/v1/order/status";
    private static String ORDER_CANCEL = "/v1/order/cancel";
    private static String WITHDRAW = "/v1/withdraw";

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

    public void withdraw(TradingCurrency currency, double amount, String destination) {
        BitfinexGetOrderRequestDto requestDto = new BitfinexGetOrderRequestDto(WITHDRAW);
        cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + WITHDRAW,
                        HttpMethod.POST,
                        new HttpEntity<>(signer.restHeaders(requestDto)),
                        Object.class);
    }

    public Map<TradingCurrency, Double> balances() {
        return null;
    }

    private OrderDto parseOrderDto(BitfinexOrderDto response) {
        return OrderDto.builder()
                .id(response.getId())
                .size(SELL.equals(response.getSide()) ? -response.getAmount() : response.getAmount())
                .price(response.getPrice())
                .status(response.getStatus())
                .build();
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 100000)
    public void ttt() {
        withdraw(TradingCurrency.Bitcoin, 0.2, "0x0000");
    }

    @Override
    public String name() {
        return BITFINEX;
    }
}
