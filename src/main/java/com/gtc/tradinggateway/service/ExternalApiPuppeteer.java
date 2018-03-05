package com.gtc.tradinggateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.gtc.tradinggateway.config.Const.Schedule.CONF_ROOT_SCHEDULE_CHILD;

/**
 * Created by Valentyn Berezin on 05.03.18.
 */
@Slf4j
@Service
public class ExternalApiPuppeteer {

    // should be used in case cookies are needed
    private final DeclaredClientsProvider clients;

    public ExternalApiPuppeteer(DeclaredClientsProvider clients) {
        this.clients = clients;
    }

    @Scheduled(fixedDelayString = "#{${" + CONF_ROOT_SCHEDULE_CHILD + "puppeteerS} * 1000}")
    public void connection() {
        try {
            clients.getClientList().stream()
                    .filter(BaseWsClient::isDisconnected)
                    .forEach(BaseWsClient::connect);
        } catch (RuntimeException ex) {
            log.error("Failed connecting", ex);
        }
    }
}
