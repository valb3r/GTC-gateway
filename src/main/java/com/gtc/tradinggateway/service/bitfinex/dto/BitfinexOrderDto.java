package com.gtc.tradinggateway.service.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by mikro on 20.02.2018.
 */
@Data
public class BitfinexOrderDto {

    private String id;
    private String symbol;
    private double price;
    private String side;

    @JsonProperty("is_live")
    private String status;

    private void setStatus(boolean isLive) {
        status = isLive ? "ACTIVE" : "NOT_ACTIVE";
    }

    @JsonProperty("remaining_amount")
    private double amount;
}
