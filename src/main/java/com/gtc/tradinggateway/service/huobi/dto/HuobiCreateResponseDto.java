package com.gtc.tradinggateway.service.huobi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by mikro on 01.04.2018.
 */
@Data
public class HuobiCreateResponseDto {

    @JsonProperty("data")
    private String orderId;
}
