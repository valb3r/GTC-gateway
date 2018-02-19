package com.gtc.tradinggateway.service.bitfinex;

import com.gtc.tradinggateway.config.BitfinexConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
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

/**
 * Created by mikro on 15.02.2018.
 */
@EnableScheduling
@Slf4j
@Service
@RequiredArgsConstructor
public class BitfinexRestClient implements Account, ManageOrders, Withdraw {

    private static String ORDERS = "/v2/auth/r/orders";

    private final BitfinexConfig cfg;
    private final BitfinexEncryptionService signer;

    public Map<TradingCurrency, Double> balances() {
        return null;
    }

    public Optional<OrderDto> get(String id) {
        return null;
    }

    public List<OrderDto> getOpen() {
        log.info(cfg.getRestBase() + ORDERS);
        BitfinexRequestDto requestDto = new BitfinexRequestDto(ORDERS);
        ResponseEntity<String[][]> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDERS,
                        HttpMethod.POST,
                        new HttpEntity<>(requestDto, signer.restHeaders(requestDto)),
                        String[][].class);
        String[][] orders = resp.getBody();
        List<OrderDto> result = new ArrayList<>();
        for (String[] order : orders) {
            result.add(parseOrderDto(order));
        }
        return result;
    }

    public void cancel(String id) {

    }

    public void withdraw(TradingCurrency currency, double amount, String destination) {

    }

    private OrderDto parseOrderDto(String[] response) {
        return OrderDto.builder()
                .id(response[2])
                .size(Double.valueOf(response[6]))
                .price(Double.valueOf(response[16]))
                .status(response[13])
                .build();
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 100000)
    public void ttt() {
        getOpen();
    }
}
