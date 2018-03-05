package com.gtc.tradinggateway.service.wex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Valentyn Berezin on 05.03.18.
 */
public class WexWithdrawRequest extends BaseWexRequest {

    @JsonProperty("coinName")
    private final String coinName;

    private final double amount;
    private final String dest;

    public WexWithdrawRequest(int nonce, String method, String coinName, double amount, String dest) {
        super(nonce, method);
        this.coinName = coinName;
        this.amount = amount;
        this.dest = dest;
    }
}
