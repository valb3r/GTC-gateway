package com.gtc.tradinggateway.service.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by mikro on 20.02.2018.
 */
@Data
public class BitfinexGetOrderRequestDto extends BitfinexRequestDto {

    @JsonProperty("order_id")
    private final long id;

    public BitfinexGetOrderRequestDto(String request, long id) {
        super(request);
        this.id = id;
    }
}
