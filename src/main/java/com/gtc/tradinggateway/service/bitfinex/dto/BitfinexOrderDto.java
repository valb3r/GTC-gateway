package com.gtc.tradinggateway.service.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gtc.model.tradinggateway.api.dto.data.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by mikro on 20.02.2018.
 */
@Data
public class BitfinexOrderDto {

    private String id;
    private String symbol;
    private BigDecimal price;
    private String side;

    private OrderStatus status;

    @JsonProperty("remaining_amount")
    private BigDecimal amount;
}
