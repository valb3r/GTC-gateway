package com.gtc.tradinggateway.service.bitfinex.dto;

import lombok.Data;

/**
 * Created by mikro on 21.02.2018.
 */
@Data
public class BitfinexBalanceItemDto {

    private String currency;
    private String type;
    private double amount;
}
