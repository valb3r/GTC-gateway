package com.gtc.tradinggateway.service.huobi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by mikro on 01.04.2018.
 */
@Data
public class HuobiCreateRequestDto extends HuobiRequestDto {

    private String source = "api";
    private String type = "buy-limit";

    @JsonProperty("account-id")
    private String accountId;

    private String amount;
    private String price;
    private String symbol;

    HuobiCreateRequestDto(String AccessKeyId) {
        super(AccessKeyId);
    }
}
