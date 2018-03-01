package com.gtc.tradinggateway.service.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.model.tradinggateway.api.dto.data.OrderStatus;
import lombok.Data;

import java.util.Map;


/**
 * Created by mikro on 24.01.2018.
 */
@Data
public class BinanceGetOrderDto {

    private static final Map<String, OrderStatus> MAPPER = ImmutableMap.<String, OrderStatus>builder()
            .put("NEW", OrderStatus.NEW)
            .put("PARTIALLY_FILLED", OrderStatus.PARTIALLY_FILLED)
            .put("FILLED", OrderStatus.FILLED)
            .put("CANCELED", OrderStatus.CANCELED)
            .put("REJECTED", OrderStatus.REJECTED)
            .put("EXPIRED", OrderStatus.EXPIRED)
            .build();

    @JsonProperty("orderId")
    private String id;

    @JsonProperty("symbol")
    private String pair;

    @JsonProperty("origQty")
    private double originalAmount;

    @JsonProperty("icebergQty")
    private double currentAmount;

    private double price;

    private String status;

    public OrderDto mapTo() {
        return OrderDto.builder()
                .id(id)
                .size(currentAmount)
                .price(price)
                .status(MAPPER.getOrDefault(status, OrderStatus.UNMAPPED))
                .statusString(status)
                .build();
    }
}
