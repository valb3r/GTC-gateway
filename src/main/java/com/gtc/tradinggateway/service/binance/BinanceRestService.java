package com.gtc.tradinggateway.service.binance;

import com.gtc.tradinggateway.config.BinanceConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.binance.dto.*;
import com.gtc.tradinggateway.service.dto.OrderDto;
import com.gtc.tradinggateway.service.dto.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by mikro on 23.01.2018.
 */
@EnableScheduling
@Service
@RequiredArgsConstructor
@Slf4j
public class BinanceRestService implements ManageOrders, Withdraw, Account, CreateOrder {

    private static final String ORDERS = "/api/v3/order";
    private static final String ALL_ORDERS = "/api/v3/allOrders";
    private static final String WITHDRAWAL = "/wapi/v3/withdraw.html";

    private final BinanceConfig cfg;
    private final BinanceEncryptionService signer;

    @Override
    public Optional<OrderDto> get(OrderRequestDto orderRequestDto) {
        BinanceRequestOrderDto orderDto = new BinanceRequestOrderDto(orderRequestDto);
        String body = orderDto.toString();
        String signedBody = getSignedBody(body);
        log.info(body);
        log.info(signedBody);
        log.info(signer.restHeaders().toString());
        log.info(cfg.getRestBase() + ORDERS + "?" + signedBody);
        ResponseEntity<BinanceGetOrderDto> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDERS + "?" + signedBody,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        BinanceGetOrderDto.class);
//        if (resp.getStatusCode().is2xxSuccessful()) {
//            return Optional.of(resp.getBody());
//        }

        return Optional.empty();
    }

    @SneakyThrows
    private String getSignedBody(String body) {
        return body + "&signature=" + URLEncoder.encode(signer.generate(body), "UTF-8");
    }

    @Scheduled(fixedDelay = 1000)
    public void ttt() {
//        OrderRequestDto orderRequestDto = new OrderRequestDto();
//        orderRequestDto.setId("1");
//        orderRequestDto.setPair("BTCUSD");
        getOpen();
    }

    @Override
    public List<OrderDto> getOpen() {
        BinanceRequestDto dto = new BinanceRequestDto();
        String body = dto.toString();
        String signedBody = getSignedBody(body);
        log.info(body);
        log.info(signedBody);
        log.info(signer.restHeaders().toString());
        log.info(cfg.getRestBase() + ALL_ORDERS + "?" + signedBody);
        ResponseEntity<BinanceGetAllOrdersDto> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ALL_ORDERS + "?" + signedBody,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        BinanceGetAllOrdersDto.class);
        return null;
    }

    @Override
    public void cancel(OrderRequestDto orderRequestDto) {
        BinanceCancelOrderRequestDto requestDto = new BinanceCancelOrderRequestDto(orderRequestDto);
        String body = requestDto.toString();
        String signedBody = getSignedBody(body);
        log.info(body);
        log.info(signedBody);
        log.info(signer.restHeaders().toString());
        cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDERS,
                        HttpMethod.DELETE,
                        new HttpEntity<>(signedBody, signer.restHeaders()), Object.class);
    }

    @Override
    public Map<TradingCurrency, Double> balances() {
        return null;
    }

    @Override
    public void withdraw(TradingCurrency currency, double amount, String destination) {
//
//        cfg.getRestTemplate()
//                .exchange(
//                        cfg.getRestBase() + WITHDRAWAL,
//                        HttpMethod.POST,
//                        new HttpEntity<>(signedBody, signer.restHeaders()), Object.class);
    }

    @Override
    public String create(TradingCurrency currency, double amount, double price) {
        return null;
    }

}
