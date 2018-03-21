package com.gtc.tradinggateway.service.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by mikro on 21.02.2018.
 */
@Data
@RequiredArgsConstructor
public class BitfinexCreateOrderWsDto {

    private String type = "EXCHANGE LIMIT";

    @JsonProperty("cid")
    private final String id;
    private final String symbol;
    private final BigDecimal amount;
    private final BigDecimal price;
}
