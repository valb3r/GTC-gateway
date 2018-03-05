package com.gtc.tradinggateway.service.wex.dto;

/**
 * Created by Valentyn Berezin on 05.03.18.
 */
public class WexCancelOrderRequest extends BaseWexRequest {

    private final long orderId;

    public WexCancelOrderRequest(int nonce, String method, long orderId) {
        super(nonce, method);
        this.orderId = orderId;
    }
}
