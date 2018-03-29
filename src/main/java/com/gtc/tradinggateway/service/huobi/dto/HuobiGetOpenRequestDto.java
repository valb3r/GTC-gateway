package com.gtc.tradinggateway.service.huobi.dto;

public class HuobiGetOpenRequestDto extends HuobiRequestDto {

    private String states = "partial-filled;partial-canceled;pre-submitted;submitted";

    public HuobiGetOpenRequestDto(String publicKey) {
        super(publicKey);
    }
}
