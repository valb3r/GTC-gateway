package com.gtc.tradinggateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

/**
 * Created by Valentyn Berezin on 20.02.18.
 */
@EnableJms
@Configuration
@Import(ActiveMQAutoConfiguration.class)
@ConditionalOnProperty("ESB_AMQ_ADDRESS")
public class JmsConfig {

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        // force topic instead of queue
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }
}
