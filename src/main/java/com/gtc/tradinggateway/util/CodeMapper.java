package com.gtc.tradinggateway.util;

import com.gtc.tradinggateway.config.BaseConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Valentyn Berezin on 04.03.18.
 */
@Slf4j
@UtilityClass
public class CodeMapper {

    public void mapAndPut(String currencyName, double amount, BaseConfig cfg, Map<TradingCurrency, Double> results) {
        Optional<TradingCurrency> currency = TradingCurrency.fromCodeRelaxed(
                currencyName,
                cfg.getCustomResponseCurrencyMapping()
        );

        currency.ifPresent(it -> results.put(it, amount));

        if (!currency.isPresent()) {
            log.warn("Failed mapping currency-code {} having amount {}", currencyName, amount);
        }
    }
}
