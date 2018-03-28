package com.gtc.tradinggateway.service.bitfinex;

import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.model.tradinggateway.api.dto.data.OrderStatus;
import com.gtc.tradinggateway.aspect.rate.IgnoreRateLimited;
import com.gtc.tradinggateway.aspect.rate.RateLimited;
import com.gtc.tradinggateway.config.BitfinexConfig;
import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.bitfinex.dto.*;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import com.gtc.tradinggateway.util.CodeMapper;
import com.gtc.tradinggateway.util.DefaultInvertHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.gtc.tradinggateway.config.Const.Clients.BITFINEX;

/**
 * Created by mikro on 15.02.2018.
 */
// FIXME: It is V1 outdated API
@Slf4j
@Service
@RequiredArgsConstructor
@RateLimited(ratePerMinute = "${app.bitfinex.ratePerM}", mode = RateLimited.Mode.CLASS)
public class BitfinexRestClient implements Withdraw, ManageOrders, Account, CreateOrder {

    private static String SELL = "sell";
    private static String ORDERS = "/v1/orders";
    private static String ORDER = "/v1/order/status";
    private static String ORDER_CANCEL = "/v1/order/cancel";
    private static String ORDER_NEW = "/v1/order/new";
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
        return Arrays.stream(orders).map(this::parseOrderDto).collect(Collectors.toList());
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
                new BitfinexWithdrawRequestDto(WITHDRAW, cfg.getCustomCurrencyName().get(currency), amount, destination);
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
        Arrays.stream(assets)
                .filter(it -> EXCHANGE_TYPE.equals(it.getType()))
                .forEach(it -> CodeMapper.mapAndPut(it.getCurrency(), it.getAmount(), cfg, results));
        return results;
    }

    private OrderDto parseOrderDto(BitfinexOrderDto response) {
        return OrderDto.builder()
                .orderId(response.getId())
                .size(SELL.equals(response.getSide())
                        ? response.getAmount().negate()
                        : response.getAmount())
                .price(response.getPrice())
                .status(getOrderStatus(response))
                .build();
    }

    private OrderStatus getOrderStatus(BitfinexOrderDto response) {
        OrderStatus result = OrderStatus.NEW;
        if (response.isCancelled()) {
            result = OrderStatus.CANCELED;
        } else if (!response.isActive()) {
            result = OrderStatus.FILLED;
        } else if (response.getExecutedAmount().compareTo(BigDecimal.ZERO) > 0) {
            return OrderStatus.PARTIALLY_FILLED;
        }
        return result;
    }

    @SneakyThrows
    public Optional<OrderCreatedDto> create(String tryToAssignId, TradingCurrency from, TradingCurrency to,
                                            BigDecimal amount, BigDecimal price) {
        PairSymbol pair = cfg.pairFromCurrency(from, to).orElseThrow(() -> new IllegalArgumentException(
                "Pair from " + from.toString() + " to " + to.toString() + " is not supported")
        );

        BigDecimal calcAmount = DefaultInvertHandler.amountFromOrig(pair, amount, price);
        BigDecimal calcPrice = DefaultInvertHandler.priceFromOrig(pair, price);

        BitfinexCreateOrderRequestDto requestDto = new BitfinexCreateOrderRequestDto(
                ORDER_NEW,
                pair.toString(),
                DefaultInvertHandler.amountToBuyOrSell(calcAmount),
                calcAmount.abs(),
                calcPrice
        );

        log.info(cfg.getMapper().writeValueAsString(requestDto));

        ResponseEntity<BitfinexOrderDto> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDER_NEW,
                        HttpMethod.POST,
                        new HttpEntity<>(signer.restHeaders(requestDto)),
                        BitfinexOrderDto.class);

        BitfinexOrderDto result = resp.getBody();

        return Optional.of(
                OrderCreatedDto.builder()
                        .assignedId(result.getId())
                        .build()
        );
    }

    @Override
    @IgnoreRateLimited
    public String name() {
        return BITFINEX;
    }
}