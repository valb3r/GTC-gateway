package com.gtc.model.tradinggateway.api.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.jms.Message;
import java.io.Serializable;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
@RequiredArgsConstructor
public abstract class AbstractMessage implements Serializable {

    protected static final String HEADER_NAME = "name";

    protected abstract String getHeader();

    private final String clientName;
    private final String id;

    @SneakyThrows
    public Message enhance(Message orig) {
        orig.setStringProperty(HEADER_NAME, getHeader());
        return orig;
    }
}
