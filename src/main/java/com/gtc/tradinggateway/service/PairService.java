package com.gtc.tradinggateway.service;

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
                                return new PairSymbol(TradingCurrency.fromCode(symbol[0]), TradingCurrency.fromCode(symbol[1]), pair[0]);
                            });
                        },
                        HashMap::putAll);
    }

    public PairSymbol fromCurrency(TradingCurrency from, TradingCurrency to) {
        String symbol = from.toString() + to.toString();
        PairSymbol pair = pairsMap.get(symbol);
        if (pair != null) {
            return pair;
        }
        String invertedSymbol = to.toString() + from.toString();
        PairSymbol invertedPair = pairsMap.get(invertedSymbol);
        return invertedPair != null ? invertedPair.invert() : null;
    }

}
