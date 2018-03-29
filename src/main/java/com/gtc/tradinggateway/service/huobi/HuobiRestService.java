package com.gtc.tradinggateway.service.huobi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.tradinggateway.aspect.rate.IgnoreRateLimited;
import com.gtc.tradinggateway.config.HuobiConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.binance.dto.BinanceGetOrderDto;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import com.gtc.tradinggateway.service.huobi.dto.HuobiGetOpenRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.*;

import static com.gtc.tradinggateway.config.Const.Clients.HUOBI;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class HuobiRestService implements ManageOrders, Withdraw, Account, CreateOrder {

    private final HuobiConfig cfg;
    private final HuobiEncryptionService signer;

    private static String ORDERS = "/v1/order/orders";

    @Override
    public Optional<OrderCreatedDto> create(String tryToAssignId, TradingCurrency from, TradingCurrency to,
                                            BigDecimal amount, BigDecimal price) {
        return Optional.empty();
    }

    @Override
    public Optional<OrderDto> get(String id) {
        return Optional.empty();
    }

    @Override
    public List<OrderDto> getOpen() {
        HuobiGetOpenRequestDto requestDto = new HuobiGetOpenRequestDto(cfg.getPublicKey());
        RestTemplate template = cfg.getRestTemplate();
        ResponseEntity<Object> resp = template
                .exchange(
                        getQueryString(HttpMethod.GET, ORDERS, requestDto),
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        Object.class);
        log.info(resp.getBody().toString());
        return new ArrayList<>();
    }

    @Override
    public void cancel(String id) {

    }

    @Override
    public Map<TradingCurrency, BigDecimal> balances() {
        return new HashMap<>();
    }

    @Override
    public void withdraw(TradingCurrency currency, BigDecimal amount, String destination) {

    }

    @Override
    @IgnoreRateLimited
    public String name() {
        return HUOBI;
    }

    private String getQueryString(HttpMethod method, String url, Object queryObj) {
        LinkedMultiValueMap<String, String> params = cfg.getMapper()
                .convertValue(queryObj, new TypeReference<LinkedMultiValueMap<String, String>>() {});
        String query = UriComponentsBuilder.fromHttpUrl(cfg.getRestBase())
                .queryParams(params)
                .build()
                .toUriString();
        return query + "&Signature=" + signer.generate(method, url, query);
    }

    @IgnoreRateLimited
    @Scheduled(initialDelay = 0, fixedDelay = 10000000)
    public void ttt() {
        getOpen();
    }
}
