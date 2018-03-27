package com.gtc.tradinggateway.service.huobi;

import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.tradinggateway.aspect.rate.IgnoreRateLimited;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.gtc.tradinggateway.config.Const.Clients.HUOBI;

@Service
public class HuobiRestService implements ManageOrders, Withdraw, Account, CreateOrder {

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
}
