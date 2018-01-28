package com.gtc.tradinggateway.service.binance.dto;

import com.gtc.tradinggateway.service.dto.OrderRequestDto;
import com.gtc.tradinggateway.util.UriFormatter;
import lombok.Data;

/**
 * Created by mikro on 28.01.2018.
 */
@Data
public class BinanceCancelOrderRequestDto extends BinanceRequestDto {

    private String symbol;

    private String orderId;

    public BinanceCancelOrderRequestDto(OrderRequestDto orderDto) {
        setSymbol(orderDto.getPair());
        setOrderId(orderDto.getId());
    }

    @Override
    public String toString() {
        UriFormatter uri = new UriFormatter();
        uri.addToUri("timestamp", String.valueOf(getTimestamp()));
        uri.addToUri("symbol", String.valueOf(getTimestamp()));
        uri.addToUri("orderId", String.valueOf(getOrderId()));
        return uri.toString();
    }

}
