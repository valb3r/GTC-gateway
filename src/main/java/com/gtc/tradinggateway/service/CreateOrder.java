package com.gtc.tradinggateway.service;

import com.gtc.tradinggateway.meta.TradingCurrency;

/**
 * Typically WSS based.
 */
public interface CreateOrder {

    /**
     * @return created order id.
     */
    String create(TradingCurrency currency, double amount, double price);
}
