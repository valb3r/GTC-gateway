package com.gtc.tradinggateway.service;

import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;

import java.math.BigDecimal;

/**
 * Typically WSS based.
 */
public interface CreateOrder extends ClientNamed {

    /**
     * @return created order id.
     */
    OrderCreatedDto create(TradingCurrency from, TradingCurrency to, BigDecimal amount, BigDecimal price);
}
