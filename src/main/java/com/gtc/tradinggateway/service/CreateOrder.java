package com.gtc.tradinggateway.service;

import com.gtc.tradinggateway.meta.TradingCurrency;

/**
 * Typically WSS based.
 */
public interface CreateOrder extends ClientNamed {

    /**
     * @return created order id.
     */
    String create(TradingCurrency from, TradingCurrency to, double amount, double price);
}
