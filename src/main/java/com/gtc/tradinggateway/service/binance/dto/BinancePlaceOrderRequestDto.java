package com.gtc.tradinggateway.service.binance.dto;

import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.util.UriFormatter;
import lombok.Data;

/**
 * Created by mikro on 01.02.2018.
 */
@Data
public class BinancePlaceOrderRequestDto extends BinanceRequestDto {

    private PairSymbol symbol;

    private String side;

    private String type = "LIMIT";

    private double quantity;

    private double price;

    private String timeInForce = "GTC";

    @Override
    public String toString() {
        UriFormatter uri = new UriFormatter();
        uri.addToUri("symbol", getSymbol());
        uri.addToUri("side", getSide());
        uri.addToUri("type", getType());
        uri.addToUri("timeInForce", getTimeInForce());
        uri.addToUri("quantity", String.valueOf(getQuantity()));
        uri.addToUri("price", String.valueOf(getPrice()));
        uri.addToUri("recvWindow", String.valueOf(getRecvWindow()));
        uri.addToUri("timestamp", String.valueOf(getTimestamp()));
        return uri.toString();
    }

    public BinancePlaceOrderRequestDto (PairSymbol symbol, double amount, double price) {
        this.symbol = symbol;
        this.side = amount < 0 ? "SELL" : "BUY";
        this.price = price;
        this.quantity = Math.abs(amount);
    }

}
