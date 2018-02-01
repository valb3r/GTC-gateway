package com.gtc.tradinggateway.service.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by mikro on 31.01.2018.
 */
@Data
public class BinanceBalanceDto {

    private BinanceBalanceAsset[] balances;

    @Data
    public static class BinanceBalanceAsset {

        @JsonProperty("asset")
        private String code;

        @JsonProperty("free")
        private Double amount;

    }

}


//{
//        "makerCommission": 15,
//        "takerCommission": 15,
//        "buyerCommission": 0,
//        "sellerCommission": 0,
//        "canTrade": true,
//        "canWithdraw": true,
//        "canDeposit": true,
//        "updateTime": 123456789,
//        "balances": [
//        {
//        "asset": "BTC",
//        "free": "4723846.89208129",
//        "locked": "0.00000000"
//        },
//        {
//        "asset": "LTC",
//        "free": "4763368.68006011",
//        "locked": "0.00000000"
//        }
//        ]
//        }