package com.gtc.tradinggateway.service.wex.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Valentyn Berezin on 04.03.18.
 */
@Getter
@Setter
public class TradeWexRequest extends BaseWexRequest {

    private final String pair;
    private final String type;
    private final double price;
    private final double amount;

    public TradeWexRequest(int nonce, String method, String pair, String type, double price, double amount) {
        super(nonce, method);
        this.pair = pair;
        this.type = type;
        this.price = price;
        this.amount = amount;
    }
}
