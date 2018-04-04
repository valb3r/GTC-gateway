package com.gtc.tradinggateway.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.collect.ImmutableList;
import com.gtc.tradinggateway.config.converters.FormHttpMessageToPojoConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import static com.gtc.tradinggateway.config.Const.CONF_ROOT_CHILD;
import static com.gtc.tradinggateway.config.Const.Clients.HUOBI;

@Getter
@Setter
@Configuration
@ConfigurationProperties(CONF_ROOT_CHILD + HUOBI)
public class HuobiConfig extends BaseConfig {

    public HuobiConfig(ConfigFactory factory) {
        mapper = factory.defaultMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        restTemplate = factory.defaultRestTemplate(mapper);
        restTemplate.setMessageConverters(ImmutableList.of(
                new MappingJackson2HttpMessageConverter(mapper)
        ));
    }
}
