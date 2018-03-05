package com.gtc.tradinggateway.service.hitbtc;

import com.gtc.tradinggateway.aspect.rate.IgnoreRateLimited;
import com.gtc.tradinggateway.aspect.rate.RateLimited;
import com.gtc.tradinggateway.config.HitbtcConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcBalanceItemDto;
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcOrderGetDto;
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcWithdrawRequestDto;
import com.gtc.tradinggateway.util.CodeMapper;
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

import static com.gtc.tradinggateway.config.Const.Clients.HITBTC;

/**
 * Validated basic functionality (get, get all, cancel)
 * 05.03.2018
 */
@Slf4j
@Service
@RequiredArgsConstructor
@RateLimited(ratePerSecond = "${app.hitbtc.ratePerS}", mode = RateLimited.Mode.CLASS)
public class HitbtcRestService implements ManageOrders, Withdraw, Account {

    private static final String ORDERS = "/order/";
    private static final String BALANCES = "/trading/balance";
    private static final String WITHDRAWAL = "/account/crypto/withdraw";

    private final HitbtcConfig cfg;
    private final HitbtcEncryptionService signer;

    @Override
    public Optional<OrderDto> get(String id) {
        ResponseEntity<HitbtcOrderGetDto> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDERS + id,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        HitbtcOrderGetDto.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            return Optional.of(resp.getBody().mapTo());
        }

        return Optional.empty();
    }

    @Override
    public List<OrderDto> getOpen() {
        ResponseEntity<HitbtcOrderGetDto[]> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDERS,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        HitbtcOrderGetDto[].class);
        return Arrays.stream(resp.getBody()).map(HitbtcOrderGetDto::mapTo).collect(Collectors.toList());

    }

    @Override
    public void cancel(String id) {
        cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDERS + id,
                        HttpMethod.DELETE,
                        new HttpEntity<>(signer.restHeaders()), Object.class);
    }

    @Override
    public Map<TradingCurrency, BigDecimal> balances() {
        ResponseEntity<HitbtcBalanceItemDto[]> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + BALANCES,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        HitbtcBalanceItemDto[].class);
        Map<TradingCurrency, BigDecimal> results = new EnumMap<>(TradingCurrency.class);
        HitbtcBalanceItemDto[] assets = resp.getBody();
        for (HitbtcBalanceItemDto asset : assets) {
            CodeMapper.mapAndPut(asset.getCurrency(), asset.getAvailable(), cfg, results);
        }
        return results;
    }

    @SneakyThrows
    @Override
    public void withdraw(TradingCurrency currency, BigDecimal amount, String destination) {
        HitbtcWithdrawRequestDto requestDto = new HitbtcWithdrawRequestDto(destination, amount, currency.toString());

        cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + WITHDRAWAL,
                        HttpMethod.POST,
                        new HttpEntity<>(requestDto, signer.restHeaders()), Object.class);
    }

    @Override
    @IgnoreRateLimited
    public String name() {
        return HITBTC;
    }
}
