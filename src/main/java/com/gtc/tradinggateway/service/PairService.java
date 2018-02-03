package com.gtc.tradinggateway.service;

import com.gtc.tradinggateway.config.BaseConfig;
import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.meta.TradingCurrency;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mikro on 02.02.2018.
 */
public class PairService {

    protected Map<String, PairSymbol> pairsMap;

    protected void parse(List<String> input) {
        pairsMap = input.stream()
                .collect(
                        HashMap::new,
                        (HashMap<String, PairSymbol> map, String val) -> {
                            String[] pair = val.split("=");
                            String[] symbol = pair[1].split("-");
                            map.computeIfAbsent(pair[0], (String mKey) -> {
                                return new PairSymbol(TradingCurrency.fromCode(symbol[0]), TradingCurrency.fromCode(symbol[1]));
                            });
                        },
                        HashMap::putAll);
    }

}
