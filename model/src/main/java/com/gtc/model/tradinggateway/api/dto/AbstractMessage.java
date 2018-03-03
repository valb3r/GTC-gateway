package com.gtc.model.tradinggateway.api.dto;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.jms.Message;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public abstract class AbstractMessage implements Serializable {

    protected static final String MIN_DECIMAL = "0.0000000000000000000001";

    protected static final String HEADER_NAME = "name";

    protected abstract String getHeader();

    @NotBlank
    private String clientName;

    @NotBlank
    private String id;

    public AbstractMessage(String clientName, String id) {
        this.clientName = clientName;

        if (null == id) {
            this.id = UUID.randomUUID().toString();
        } else {
            this.id = id;
        }
    }

    @SneakyThrows
    public Message enhance(Message orig) {
        orig.setStringProperty(HEADER_NAME, getHeader());
        return orig;
    }
}
