package com.gtc.tradinggateway.service.binance.dto;

import com.gtc.tradinggateway.util.UriFormatter;
import lombok.Data;

/**
 * Created by mikro on 28.01.2018.
 */
@Data
public class BinanceRequestDto {

    protected long timestamp = System.currentTimeMillis();
    protected int recvWindow = 5000;

    public String toString() {
        UriFormatter uri = new UriFormatter();
        uri.addToUri("timestamp", String.valueOf(getTimestamp()));
        uri.addToUri("recvWindow", String.valueOf(getRecvWindow()));
        return uri.toString();
    }
}
