package com.gtc.tradinggateway.service.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


/**
 * Created by mikro on 24.01.2018.
 */
@Data
public class BinanceGetOrderDto {

    private String id;

    @JsonProperty("symbol")
    private String pair;

    @JsonProperty("origQty")
    private double originalAmount;

    @JsonProperty("icebergQty")
    private double currentAmount;

    private double price;

    private String status;

}
