package com.gtc.tradinggateway.service.huobi.dto;

import lombok.Data;

/**
 * Created by mikro on 01.04.2018.
 */
@Data
public class HuobiOrderDto {

    private Number orderId;
    private String symbol;
    private String price;
    private String amount;
    private String state;

    public OrderDto mapTo() {
        return OrderDto.builder()
                .orderId(orderId)
                .size(amount)
                .price(price)
                .status(state)
                .build();
    }
}
