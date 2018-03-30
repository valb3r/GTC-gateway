package com.gtc.tradinggateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;

/**
 * Created by Valentyn Berezin on 20.02.18.
 */
@EnableJms
@Configuration
@Import(ActiveMQAutoConfiguration.class)
@ConditionalOnProperty("ESB_AMQ_ADDRESS")
public class JmsConfig {
}
