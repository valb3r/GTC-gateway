package com.gtc.tradinggateway.app;

import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {
        "com.gtc.tradinggateway.aspect",
        "com.gtc.tradinggateway.service",
        "com.gtc.tradinggateway.controller",
        "com.gtc.tradinggateway.config"
}, exclude = ActiveMQAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AppInitializer {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        SpringApplication app = new SpringApplication(AppInitializer.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}

