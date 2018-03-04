package com.gtc.tradinggateway.service.wex;

import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.tradinggateway.aspect.rate.RateLimited;
import com.gtc.tradinggateway.config.WexConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.gtc.tradinggateway.config.Const.Clients.WEX;

/**
 * Created by Valentyn Berezin on 04.03.18.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@RateLimited(ratePerSecond = "${app.wex.ratePerS}", mode = RateLimited.Mode.CLASS)
public class WexRestService implements ManageOrders, Withdraw, Account, CreateOrder {

    private static final long NONCE_BEGIN = 1520154667151L;

    private static final String GET_INFO = "getInfo";

    private final WexConfig cfg;
    private final WexEncryptionService signer;

    @Override
    public Map<TradingCurrency, Double> balances() {
        return null;
    }

    @Override
    public String create(TradingCurrency from, TradingCurrency to, double amount, double price) {
        return null;
    }

    @Override
    public Optional<OrderDto> get(String id) {
        return Optional.empty();
    }

    @Override
    public List<OrderDto> getOpen() {
        return null;
    }

    @Override
    public void cancel(String id) {

    }

    @Override
    public void withdraw(TradingCurrency currency, double amount, String destination) {

    }

    @Override
    public String name() {
        return WEX;
    }

    // sufficient for 9940 days and rate 10 r/s - it has max of 2^32 as per docs
    private static long nonce() {
        return (System.currentTimeMillis() - NONCE_BEGIN) / 100;
    }
}
