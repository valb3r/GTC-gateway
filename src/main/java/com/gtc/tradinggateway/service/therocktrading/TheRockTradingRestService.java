package com.gtc.tradinggateway.service.therocktrading;

import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.tradinggateway.aspect.rate.IgnoreRateLimited;
import com.gtc.tradinggateway.aspect.rate.RateLimited;
import com.gtc.tradinggateway.config.TheRockTradingConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.gtc.tradinggateway.config.Const.Clients.THEROCKTRADING;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
@RateLimited(ratePerMinute = "${app.therocktrading.ratePerM}", mode = RateLimited.Mode.CLASS)
public class TheRockTradingRestService implements Account, CreateOrder, ManageOrders, Withdraw {

    private static String BALANCE = "/v1/balances";

    private final TheRockTradingConfig cfg;
    private final TheRockTradingEncryptionService signer;

    public Map<TradingCurrency, BigDecimal> balances() {
        String url = cfg.getRestBase() + BALANCE;
        log.info(url);
        log.info(signer.restHeaders(url).toString());
        ResponseEntity<Object> resp = cfg.getRestTemplate()
                .exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders(url)),
                        Object.class);
        log.info(resp.getBody().toString());
        return new HashMap<>();
    }

    public Optional<OrderCreatedDto> create(String tryToAssignId,
                                            TradingCurrency from,
                                            TradingCurrency to,
                                            BigDecimal amount,
                                            BigDecimal price
    ) {
        return Optional.empty();
    }

    public Optional<OrderDto> get(String id) {
        return Optional.empty();
    }

    public List<OrderDto> getOpen() {
        return new ArrayList<>();
    }

    public void cancel(String id) {

    }

    public void withdraw(TradingCurrency currency, BigDecimal amount, String destination) {

    }

    @IgnoreRateLimited
    public String name() {
        return THEROCKTRADING;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 150000)
    public void ttt() {
        balances();
    }

}
