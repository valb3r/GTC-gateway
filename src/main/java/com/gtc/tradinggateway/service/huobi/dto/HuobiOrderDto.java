package com.gtc.tradinggateway.service.huobi.dto;

import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.model.tradinggateway.api.dto.data.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by mikro on 01.04.2018.
 */
@Data
public class HuobiOrderDto {

    private Number orderId;
    private String symbol;
    private BigDecimal price;
    private BigDecimal amount;
    private OrderStatus state;

    public OrderDto mapTo() {
        return OrderDto.builder()
                .orderId(orderId.toString())
                .size(amount)
                .price(price)
                .status(state)
                .build();
    }
}
