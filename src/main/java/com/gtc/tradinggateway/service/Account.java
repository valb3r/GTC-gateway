package com.gtc.tradinggateway.service;

import com.gtc.tradinggateway.meta.TradingCurrency;

import java.util.Map;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
public interface Account extends ClientNamed {

    Map<TradingCurrency, Double> balances();
}
