package com.gtc.tradinggateway.service.hitbtc.dto;

import lombok.Data;

/**
 * Created by mikro on 13.02.2018.
 */
@Data
public class HitbtcBalanceItemDto {

    private String currency;
    private double available;
}
