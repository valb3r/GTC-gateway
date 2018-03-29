package com.gtc.tradinggateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;

import javax.jms.ConnectionFactory;

/**
 * Created by Valentyn Berezin on 20.02.18.
 */
@EnableJms
@Configuration
@Import(ActiveMQAutoConfiguration.class)
@ConditionalOnProperty("ESB_AMQ_ADDRESS")
public class JmsConfig {

    public static final String INBOUND = "INBOUND";

    @Bean(name = INBOUND)
    public SimpleJmsListenerContainerFactory inboundFactory(ConnectionFactory connectionFactory) {
        SimpleJmsListenerContainerFactory inboundFactory = new SimpleJmsListenerContainerFactory();
        inboundFactory.setConnectionFactory(connectionFactory);
        return inboundFactory;
    }
}
