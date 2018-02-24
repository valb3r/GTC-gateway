package com.gtc.tradinggateway.config;

import com.gtc.tradinggateway.meta.TradingCurrency;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gtc.tradinggateway.config.Const.CONF_ROOT_CHILD;
import static com.gtc.tradinggateway.config.Const.Clients.BITFINEX;

/**
 * Created by mikro on 15.02.2018.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(CONF_ROOT_CHILD + BITFINEX)
public class BitfinexConfig extends BaseConfig {

    protected Map<TradingCurrency, String> symbols;

    public void setSymbols(List<String> list) {
        symbols = list.stream()
                .collect(
                        HashMap::new,
                        (HashMap<TradingCurrency, String> map, String val) -> {
                            String[] pair = val.split("=");
                            map.put(TradingCurrency.fromCode(pair[0]), pair[1]);
                        },
                        HashMap::putAll);
    }

    public BitfinexConfig() {
        mapper = ConfigFactory.defaultMapper();
        restTemplate = ConfigFactory.defaultRestTemplate(mapper);
    }
}
