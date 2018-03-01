package com.gtc.tradinggateway.service.binance.dto;

import com.gtc.tradinggateway.util.UriFormatter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by mikro on 25.01.2018.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BinanceRequestOrderDto extends BinanceRequestDto {

    private String symbol;
    private String orderId;
    private String origClientOrderId = "1";

    @Override
    public String toString() {
        UriFormatter uri = new UriFormatter();
        uri.addToUri("symbol", getSymbol());
        uri.addToUri("orderId", getOrderId());
        uri.addToUri("timestamp", String.valueOf(getTimestamp()));
        return uri.toString();
    }

    public BinanceRequestOrderDto(String id) {
        String[] parsedId = id.split("\\.");
        symbol = parsedId[0];
        orderId = parsedId[1];
    }
}
