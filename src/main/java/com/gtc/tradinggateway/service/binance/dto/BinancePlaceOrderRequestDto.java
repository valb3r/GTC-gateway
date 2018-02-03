package com.gtc.tradinggateway.service.binance.dto;

import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.util.UriFormatter;
import lombok.Data;

/**
 * Created by mikro on 01.02.2018.
 */


//symbol	LTCBTC
//        side	BUY
//        type	LIMIT
//        timeInForce	GTC
//        quantity	1
//        price	0.1
//        recvWindow	5000
//        timestamp	1499827319559

@Data
public class BinancePlaceOrderRequestDto extends BinanceRequestDto {

    private String symbol;

    private String side;

    private String type = "LIMIT";

    private double quantity;

    private double price;

    private String timeInForce = "UTC";

    @Override
    public String toString() {
        UriFormatter uri = new UriFormatter();
        uri.addToUri("symbol", getSymbol());
        uri.addToUri("side", getSide());
        uri.addToUri("type", getType());
        uri.addToUri("quantity", String.valueOf(getQuantity()));
        uri.addToUri("price", String.valueOf(getPrice()));
        uri.addToUri("timestamp", String.valueOf(getTimestamp()));
        uri.addToUri("recvWindow", String.valueOf(getRecvWindow()));
        uri.addToUri("timeInForce", getTimeInForce());
        return uri.toString();
    }

    public BinancePlaceOrderRequestDto (TradingCurrency from, TradingCurrency to, double amount, double price) {
        this.symbol = from.toString();
        this.side = amount < 0 ? "SELL" : "BUY";
        this.price = price;
        this.quantity = amount;
    }

}
