package com.gtc.tradinggateway.service.huobi.dto;

/**
 * Created by mikro on 01.04.2018.
 */
public class HuobiWithdrawalRequestDto extends HuobiRequestDto {

    private String address;
    private String amount;
    private String currency;

    public HuobiWithdrawalRequestDto(String accessKey, String address, String amount, String currency) {
        super(accessKey);
        this.address = address;
        this.amount = amount;
        this.currency = currency;
    }
}
