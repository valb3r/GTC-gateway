package com.gtc.model.tradinggateway.api.dto.response.create;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
public class CreateOrderResponse extends AbstractMessage {

    private static final String HEADER = "resp.create";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    private final String orderId;

    @Builder
    public CreateOrderResponse(String clientName, String id, String orderId) {
        super(clientName, id);
        this.orderId = orderId;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
