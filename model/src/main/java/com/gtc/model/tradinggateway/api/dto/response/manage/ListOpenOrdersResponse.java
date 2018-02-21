package com.gtc.model.tradinggateway.api.dto.response.manage;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
public class ListOpenOrdersResponse extends AbstractMessage {

    private static final String HEADER = "resp.listOpen";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    private final List<OrderDto> orders;

    @Builder
    public ListOpenOrdersResponse(String clientName, String id, List<OrderDto> orders) {
        super(clientName, id);
        this.orders = orders;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
