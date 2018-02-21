package com.gtc.model.tradinggateway.api.dto.response.manage;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
public class CancelOrderResponse extends AbstractMessage {

    private static final String HEADER = "resp.cancel";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    private final String orderId;

    @Builder
    public CancelOrderResponse(String clientName, String id, String orderId) {
        super(clientName, id);
        this.orderId = orderId;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
