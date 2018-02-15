package com.gtc.tradinggateway.service.bitfinex;

import com.gtc.tradinggateway.config.BitfinexConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by mikro on 15.02.2018.
 */
@Service
@RequiredArgsConstructor
public class BitfinexRestClient implements Account, ManageOrders, Withdraw {

    private static String ORDERS = "/orders";

    private final BitfinexConfig cfg;
    private final BitfinexEncryptionService signer;

    public Map<TradingCurrency, Double> balances() {
        return null;
    }

    public Optional<OrderDto> get(String id) {
        return null;
    }

    public List<OrderDto> getOpen() {
        ResponseEntity<String[][]> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDERS,
                        HttpMethod.POST,
                        new HttpEntity<>(signer.restHeaders(new Object())),
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
        OrderDto dto = new OrderDto();
        dto.setId(response[2]);
        dto.setSize(Double.valueOf(response[6]));
        dto.setPrice(Double.valueOf(response[16]));
        dto.setStatus(response[13]);
        return dto;
    }
}
