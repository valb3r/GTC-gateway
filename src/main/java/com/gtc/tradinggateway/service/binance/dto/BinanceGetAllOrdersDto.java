package com.gtc.tradinggateway.service.binance.dto;

import lombok.Data;

/**
 * Created by mikro on 28.01.2018.
 */
@Data
public class BinanceGetAllOrdersDto {

    private BinanceGetAllOrdersItemDto[] list;

    @Data
    public static class BinanceGetAllOrdersItemDto {

        private String id;
        private String symbol;
        private double price;
        private double origQty;
        private double icebergQty;
        private String status;
        private String side;

    }

}
