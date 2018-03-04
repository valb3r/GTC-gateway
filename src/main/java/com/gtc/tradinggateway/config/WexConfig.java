package com.gtc.tradinggateway.config;

import com.google.common.collect.ImmutableList;
import com.gtc.tradinggateway.config.converters.FormHttpMessageToPojoConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.gtc.tradinggateway.config.Const.CONF_ROOT_CHILD;
import static com.gtc.tradinggateway.config.Const.Clients.WEX;

/**
 * Created by Valentyn Berezin on 04.03.18.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(CONF_ROOT_CHILD + WEX)
public class WexConfig extends BaseConfig {

    public WexConfig(ConfigFactory factory) {
        mapper = factory.defaultMapper();
        restTemplate = factory.defaultRestTemplate(mapper);
        restTemplate.setMessageConverters(ImmutableList.of(new FormHttpMessageToPojoConverter(mapper)));
    }
}
