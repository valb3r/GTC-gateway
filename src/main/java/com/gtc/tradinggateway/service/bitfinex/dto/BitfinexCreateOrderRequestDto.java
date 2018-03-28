package com.gtc.tradinggateway.service.bitfinex.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BitfinexCreateOrderRequestDto extends BitfinexRequestDto {

    private String type = "exchange limit";
    private String exchange = "bitfinex";

    private String symbol;
    private String side;
    private String amount;
    private String price;

    public BitfinexCreateOrderRequestDto(String request, String symbol, String side, BigDecimal amount, BigDecimal price) {
        super(request);
        this.symbol = symbol;
        this.side = side;
        this.amount = amount.toString();
        this.price = price.toString();
    }
}
