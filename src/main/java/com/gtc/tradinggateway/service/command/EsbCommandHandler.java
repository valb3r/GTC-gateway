package com.gtc.tradinggateway.service.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

/**
 * Created by Valentyn Berezin on 20.02.18.
 */
@Slf4j
@Service
public class EsbCommandHandler {

    private static final String CREATE_QUEUE = "${app.jms.queue.create}";
    private static final String MANAGE_QUEUE = "${app.jms.queue.manage}";
    private static final String WITHDRAW_QUEUE = "${app.jms.queue.withdraw}";

    @SendTo(CREATE_QUEUE)
    @JmsListener(destination = CREATE_QUEUE)
    public void create() {
        log.info("Request to create order");
    }

    @SendTo(MANAGE_QUEUE)
    @JmsListener(destination = MANAGE_QUEUE)
    public void manage() {
        log.info("Request to manage");
    }

    @SendTo(WITHDRAW_QUEUE)
    @JmsListener(destination = WITHDRAW_QUEUE)
    public void withdraw() {
        log.info("Request to withdraw");
    }
}
