package com.gtc.tradinggateway.service;

import com.gtc.tradinggateway.meta.TradingCurrency;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
public interface Withdraw {

    void withdraw(TradingCurrency currency, double amount, String destination);
}
