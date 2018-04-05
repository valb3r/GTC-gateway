package com.gtc.tradinggateway.service.huobi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created by mikro on 01.04.2018.
 */
@Data
@RequiredArgsConstructor
public class HuobiWithdrawalRequestDto {

    @JsonProperty("address")
    private final String address;

    @JsonProperty("amount")
    private final String amount;

    @JsonProperty("currency")
    private final String currency;
}
