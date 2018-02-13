package com.gtc.tradinggateway.service.hitbtc;

import com.gtc.tradinggateway.config.HitbtcConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.ManageOrders;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by mikro on 12.02.2018.
 */
@EnableScheduling
@Service
@RequiredArgsConstructor
@Slf4j
public class HitbtcRestService implements ManageOrders, Withdraw, Account {

    private String ORDERS = "/order/";
    private String BALANCES = "/trading/balance";
    private String WITHDRAWAL = "/account/crypto/withdraw";

    private final HitbtcConfig cfg;
    private final HitbtcEncryptionService signer;

    @Override
    public Optional<OrderDto> get(String id) {
        log.info(cfg.getRestBase() + ORDERS + id);
        log.info(signer.restHeaders().toString());
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
        HitbtcOrderGetDto[] list = resp.getBody();
        List<OrderDto> result = new ArrayList<>();
        for (HitbtcOrderGetDto respDto : list) {
            result.add(respDto.mapTo());
        }
        return result;

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
                        asset.getCurrency().toString(), String.valueOf(asset.getAvailable()));
            }
        }
        return results;
    }

    @SneakyThrows
    @Override
    public void withdraw(TradingCurrency currency, double amount, String destination) {
        HitbtcWithdrawRequestDto requestDto = new HitbtcWithdrawRequestDto(destination, amount, currency.toString());
        Map<String, String> map = cfg.getMapper().convertValue(requestDto, Map.class);
        cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + WITHDRAWAL,
                        HttpMethod.POST,
                        new HttpEntity<Map<String, String>>(map, signer.restHeaders()), Object.class);
    }

    @Scheduled(fixedRate=5000)
    public void ttt() {
        withdraw(TradingCurrency.Bitcoin, 0.2, "0x000");
    }


}
