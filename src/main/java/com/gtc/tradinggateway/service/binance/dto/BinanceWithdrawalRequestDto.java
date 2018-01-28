package com.gtc.tradinggateway.service.binance.dto;

/**
 * Created by mikro on 28.01.2018.
 */
public class BinanceWithdrawalRequestDto extends BinanceRequestDto {

    private String asset;
    private String address;
    private String addressTag;
    private String amount;
    private int recvWindow;
    private String name;
    private long timestamp;

}
