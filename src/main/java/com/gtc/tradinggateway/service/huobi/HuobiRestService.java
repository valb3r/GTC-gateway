package com.gtc.tradinggateway.service.huobi;

import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.tradinggateway.aspect.rate.IgnoreRateLimited;
import com.gtc.tradinggateway.config.HuobiConfig;
import com.gtc.tradinggateway.config.converters.FormHttpMessageToPojoConverter;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import com.gtc.tradinggateway.service.huobi.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.gtc.tradinggateway.config.Const.Clients.HUOBI;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class HuobiRestService implements ManageOrders, Withdraw, Account, CreateOrder {

    private final HuobiConfig cfg;
    private final HuobiEncryptionService signer;

    private static String ORDERS = "/v1/order/orders/";
    private static String CREATE_ORDER = ORDERS + "place";
    private static String CANCEL_ORDER = "/submitcancel";
    private static String WITHDRAWAL = "/v1/dw/withdraw/api/create";

    @Override
    public Optional<OrderCreatedDto> create(String tryToAssignId, TradingCurrency from, TradingCurrency to,
                                            BigDecimal amount, BigDecimal price) {
        HuobiCreateRequestDto requestDto = new HuobiCreateRequestDto(cfg.getPublicKey());
        RestTemplate template = cfg.getRestTemplate();
        ResponseEntity<HuobiCreateResponseDto> resp = template
                .exchange(
                        getQueryString(HttpMethod.POST, CREATE_ORDER, requestDto),
                        HttpMethod.POST,
                        new HttpEntity<>(signer.restHeaders()),
                        HuobiCreateResponseDto.class
                );

        return Optional.of(
                OrderCreatedDto.builder()
                        .assignedId(resp.getBody().getOrderId())
                        .build());
    }

    @Override
    public Optional<OrderDto> get(String id) {
        HuobiRequestDto requestDto = new HuobiRequestDto(cfg.getPublicKey());
        RestTemplate template = cfg.getRestTemplate();
        ResponseEntity<HuobiGetResponseDto> resp = template
                .exchange(
                        getQueryString(HttpMethod.GET, ORDERS + id, requestDto),
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        HuobiGetResponseDto.class
                );
        return Optional.of(resp.getBody()
                .getOrder()
                .mapTo());
    }

    @Override
    public List<OrderDto> getOpen() {
        HuobiGetOpenRequestDto requestDto = new HuobiGetOpenRequestDto(cfg.getPublicKey());
        RestTemplate template = cfg.getRestTemplate();
        log.info(getQueryString(HttpMethod.GET, ORDERS, requestDto));
        ResponseEntity<HuobiGetOpenResponseDto> resp = template
                .exchange(
                        getQueryString(HttpMethod.GET, ORDERS, requestDto),
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        HuobiGetOpenResponseDto.class);
        return resp.getBody()
                .getOrders()
                .stream()
                .map(order -> order.mapTo())
                .collect(Collectors.toList());
    }

    @Override
    public void cancel(String id) {
        HuobiRequestDto requestDto = new HuobiRequestDto(cfg.getPublicKey());
        RestTemplate template = cfg.getRestTemplate();
        template.exchange(
                getQueryString(HttpMethod.POST, ORDERS + id + CANCEL_ORDER, requestDto),
                HttpMethod.POST,
                new HttpEntity<>(signer.restHeaders()),
                Object.class);
    }

    @Override
    public Map<TradingCurrency, BigDecimal> balances() {
        return new HashMap<>();
    }

    @Override
    public void withdraw(TradingCurrency currency, BigDecimal amount, String destination) {
        HuobiWithdrawalRequestDto requestDto = new HuobiWithdrawalRequestDto(
                cfg.getPublicKey(),
                destination,
                amount.toString(),
                currency.toString());
        RestTemplate template = cfg.getRestTemplate();
        template.exchange(
                getQueryString(HttpMethod.POST, WITHDRAWAL, requestDto),
                HttpMethod.POST,
                new HttpEntity<>(signer.restHeaders()),
                Object.class);
    }

    @Override
    @IgnoreRateLimited
    public String name() {
        return HUOBI;
    }

    private String getQueryString(HttpMethod method, String url, Object queryObj) {
        String states = "&states=partial-filled;partial-canceled;pre-submitted;submitted";
        String query = FormHttpMessageToPojoConverter.pojoSerialize(cfg.getMapper(), queryObj, null) + states;
        return cfg.getRestBase() + url + "?" + query + "&Signature=" + signer.generate(method, url, query);
    }

    private String getAccountId() {
        return "test";
    }

    @IgnoreRateLimited
    @Scheduled(initialDelay = 0, fixedDelay = 10000000)
    public void ttt() {
        getOpen();
    }
}
