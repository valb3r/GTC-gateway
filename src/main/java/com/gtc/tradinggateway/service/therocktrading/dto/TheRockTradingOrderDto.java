package com.gtc.tradinggateway.service.therocktrading.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.model.tradinggateway.api.dto.data.OrderStatus;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TheRockTradingOrderDto {

    private static String BUY = "buy";

    private String id;

    @JsonProperty("fund_id")
    private String pair;

    private String side;
    private BigDecimal price;
    private BigDecimal amount;
    private String status;

    public OrderDto mapTo() {
        return OrderDto.builder()
                .orderId(pair + "." + id)
                .size(BUY.equals(side.toLowerCase()) ? amount : amount.negate())
                .price(price)
                .statusString(status)
                .status(parseStatus(status))
                .build();
    }

    public OrderCreatedDto mapToCreate() {
        return OrderCreatedDto.builder()
                .assignedId(pair + "." + id)
                .build();
    }

    private static OrderStatus parseStatus(String status) {
        switch (status) {
            case "active":
                return OrderStatus.NEW;
            case "conditional":
                return OrderStatus.UNMAPPED;
            case "executed":
                return OrderStatus.FILLED;
            case "deleted":
                return OrderStatus.CANCELED;
            default:
                return OrderStatus.UNMAPPED;
        }
    }
}


