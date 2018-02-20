package com.gtc.tradinggateway.service.hitbtc;

import com.gtc.tradinggateway.config.HitbtcConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CancelOrder;
import com.gtc.tradinggateway.service.GetOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.dto.OrderDto;
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcBalanceItemDto;
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcOrderGetDto;
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcWithdrawRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.gtc.tradinggateway.config.Const.Clients.HITBTC;

/**
 * Created by mikro on 12.02.2018.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HitbtcRestService implements GetOrders, Withdraw, Account, CancelOrder {

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
    public Map<TradingCurrency, Double> balances() {
        ResponseEntity<HitbtcBalanceItemDto[]> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + BALANCES,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        HitbtcBalanceItemDto[].class);
        Map<TradingCurrency, Double> results = new EnumMap<>(TradingCurrency.class);
        HitbtcBalanceItemDto[] assets = resp.getBody();
        for (HitbtcBalanceItemDto asset : assets) {
            try {
                results.put(TradingCurrency.fromCode(asset.getCurrency()), asset.getAvailable());
            } catch (RuntimeException ex) {
                log.error(
                        "Failed mapping currency-code {} having amount {}",
                        asset.toString(), String.valueOf(asset.getAvailable()));
            }
        }
        return results;
    }

    @SneakyThrows
    @Override
    public void withdraw(TradingCurrency currency, double amount, String destination) {
        HitbtcWithdrawRequestDto requestDto = new HitbtcWithdrawRequestDto(destination, amount, currency.toString());

        cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + WITHDRAWAL,
                        HttpMethod.POST,
                        new HttpEntity<>(requestDto, signer.restHeaders()), Object.class);
    }

    @Override
    public String name() {
        return HITBTC;
    }
}
