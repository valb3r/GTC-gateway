package com.gtc.tradinggateway.config;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.ConnectionFactory;

/**
 * Created by Valentyn Berezin on 20.02.18.
 */
@EnableJms
@Configuration
@Import(ArtemisAutoConfiguration.class)
@ConditionalOnProperty("ESB_AMQ_ADDRESS")
public class JmsConfig {

    @Bean
    @Primary
    public ConnectionFactory connectionFactory(@Value("${spring.artemis.pool}") int poolSize,
                                               ActiveMQConnectionFactory connectionFactory) {
        CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
        factory.setSessionCacheSize(poolSize);
        return factory;
    }
}
