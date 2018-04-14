package com.gtc.tradinggateway.service.huobi.dto;

import lombok.Getter;

/**
 * Created by Valentyn Berezin on 14.04.18.
 */
@Getter
public class HuobiGetOrderList extends HuobiRequestDto {

    private final String symbol;
    private final String states = "pre-submitted,submitted,partial-filled";

    public HuobiGetOrderList(String accessKeyId, String symbol) {
        super(accessKeyId);
        this.symbol = symbol;
    }
}
