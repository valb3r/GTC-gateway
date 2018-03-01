package com.gtc.model.tradinggateway.api.dto.response.manage;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import lombok.*;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class GetOrderResponse extends AbstractMessage {

    private static final String HEADER = "resp.get";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    private OrderDto order;

    @Builder
    public GetOrderResponse(String clientName, String id, OrderDto order) {
        super(clientName, id);
        this.order = order;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
