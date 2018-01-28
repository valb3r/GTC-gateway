package com.gtc.tradinggateway.service.gdax;

import com.gtc.tradinggateway.config.GdaxConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.dto.OrderDto;
import com.gtc.tradinggateway.service.dto.OrderRequestDto;
import com.gtc.tradinggateway.service.gdax.dto.GdaxGetOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
@Service
@RequiredArgsConstructor
public class GdaxRestService implements ManageOrders, Withdraw, Account {

    private static final String ORDERS = "/orders";

    private final GdaxConfig cfg;
    private final GdaxEncryptionService signer;

    @Override
    public Optional<OrderDto> get(OrderRequestDto orderGet) {
        String id = orderGet.getId();
        String relUrl = ORDERS + "/" + id;
        ResponseEntity<GdaxGetOrderDto> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + relUrl,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders(relUrl, HttpMethod.GET.name(), "")),
                        GdaxGetOrderDto.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            return Optional.of(resp.getBody().map());
        }

        return Optional.empty();
    }

    @Override
    public List<OrderDto> getOpen() {
        return null;
    }

    @Override
    public void cancel(OrderRequestDto orderGet) {

    }

    @Override
    public Map<TradingCurrency, Double> balances() {
        return null;
    }

    @Override
    public void withdraw(TradingCurrency currency, double amount, String destination) {

    }
}
