package com.gtc.tradinggateway.service.huobi.dto;

import lombok.Data;

@Data
public class HuobiGetOpenRequestDto extends HuobiRequestDto {

//    private String states = "partial-filled;partial-canceled;pre-submitted;submitted";

    public HuobiGetOpenRequestDto(String publicKey) {
        super(publicKey);
    }
}
