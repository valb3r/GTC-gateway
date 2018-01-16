package com.gtc.tradinggateway.app;

import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {
        "com.gtc.tradinggateway.service",
        "com.gtc.tradinggateway.controller",
        "com.gtc.tradinggateway.config"
})
public class AppInitializer {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        SpringApplication app = new SpringApplication(AppInitializer.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}

