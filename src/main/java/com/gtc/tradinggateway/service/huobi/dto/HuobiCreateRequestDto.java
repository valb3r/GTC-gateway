package com.gtc.tradinggateway.service.huobi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by mikro on 01.04.2018.
 */
@Data
@RequiredArgsConstructor
public class HuobiCreateRequestDto {

    private String source = "api";

    @JsonProperty("account-id")
    private final String accountId;

    private final String type;
    private final BigDecimal amount;
    private final BigDecimal price;
    private final String symbol;
}
