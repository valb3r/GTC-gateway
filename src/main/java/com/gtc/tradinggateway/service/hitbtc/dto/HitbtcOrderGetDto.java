package com.gtc.tradinggateway.service.hitbtc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.model.tradinggateway.api.dto.data.OrderStatus;
import lombok.Data;

import java.util.Map;

/**
 * Created by mikro on 13.02.2018.
 */
@Data
public class HitbtcOrderGetDto {

    public static final String SELL = "sell";

    private static final Map<String, OrderStatus> MAPPER = ImmutableMap.<String, OrderStatus>builder()
            .put("new", OrderStatus.NEW)
            .put("partiallyFilled", OrderStatus.PARTIALLY_FILLED)
            .put("filled", OrderStatus.FILLED)
            .put("canceled", OrderStatus.CANCELED)
            .put("expired", OrderStatus.EXPIRED)
            .build();

    @JsonProperty("clientOrderId")
    private String id;
    private String symbol;
    private double quantity;
    private String status;
    private double price;

    private String side;

    public OrderDto mapTo () {
        return OrderDto.builder()
                .orderId(id)
                .size(SELL.equals(side) ? -quantity : quantity)
                .price(price)
                .status(MAPPER.getOrDefault(status, OrderStatus.UNMAPPED))
                .statusString(status)
                .build();
    }
}
