package com.gtc.tradinggateway.service.hitbtc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gtc.tradinggateway.service.dto.OrderDto;
import lombok.Data;

/**
 * Created by mikro on 13.02.2018.
 */
@Data
public class HitbtcOrderGetDto {

    @JsonProperty("clientOrderId")
    private String id;
    private String symbol;
    private double quantity;
    private String status;
    private double price;
    private String side;

    public static String SELL = "sell";

    public OrderDto mapTo () {
        return OrderDto.builder()
                .id(id)
                .size(SELL.equals(side) ? -quantity : quantity)
                .price(price)
                .status(status)
                .build();
    }
}
