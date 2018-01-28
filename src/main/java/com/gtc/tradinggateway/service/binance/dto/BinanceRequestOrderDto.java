package com.gtc.tradinggateway.service.binance.dto;

import com.gtc.tradinggateway.service.dto.OrderRequestDto;
import com.gtc.tradinggateway.util.UriFormatter;
import lombok.Data;

/**
 * Created by mikro on 25.01.2018.
 */
@Data
public class BinanceRequestOrderDto extends BinanceRequestDto {

    private String symbol;
    private String orderId;

    @Override
    public String toString() {
        UriFormatter uri = new UriFormatter();
        uri.addToUri("symbol", getSymbol());
        uri.addToUri("orderId", getOrderId());
        uri.addToUri("timestamp", String.valueOf(getTimestamp()));
        return uri.toString();
    }

    public BinanceRequestOrderDto(OrderRequestDto orderRequestDto) {
        symbol = orderRequestDto.getPair();
        orderId = orderRequestDto.getId();
    }

}
