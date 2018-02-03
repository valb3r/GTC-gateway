package com.gtc.tradinggateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.meta.TradingCurrency;
import lombok.Data;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
@Data
public class BaseConfig {

    protected String wsBase;
    protected String restBase;
    protected String passphrase;
    protected String publicKey;
    protected String secretKey;

    protected ObjectMapper mapper = new ObjectMapper();

    protected RestTemplate restTemplate = new RestTemplate();

    protected int disconnectIfInactiveS = 10;

    protected Map<String, PairSymbol> pairs;

    protected void setPairs(List<String> list) {
        pairs = parse(list);
    }

    protected Map<String, PairSymbol> parse(List<String> input) {
        return input.stream()
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
        PairSymbol pair = pairs.get(symbol);
        if (pair != null) {
            return pair;
        }
        String invertedSymbol = to.toString() + from.toString();
        PairSymbol invertedPair = pairs.get(invertedSymbol);
        return invertedPair != null ? invertedPair.invert() : null;
    }

}
