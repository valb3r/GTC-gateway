package com.gtc.tradinggateway.service.huobi.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HuobiBalanceResponseDto {

    private Data data;

    @lombok.Data
    public static class Data {

        private List<BalanceItem> list;
    }

    @lombok.Data
    public static class BalanceItem {

        private String currency;

        @JsonProperty("balance")
        private BigDecimal amount;
    }
}
