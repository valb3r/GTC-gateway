package com.gtc.tradinggateway.service.binance.dto;

import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.util.UriFormatter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by mikro on 01.02.2018.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BinancePlaceOrderRequestDto extends BinanceRequestDto {

    private PairSymbol symbol;

    private OrderSide side;

    private String type = "LIMIT";

    private double quantity;

    private double price;

    private String timeInForce = "GTC";

    public enum OrderSide {
        Buy("BUY"),
        Sell("SELL");

        @Getter
        private final String side;

        OrderSide(String side) {
            this.side = side;
        }

        @Override
        public String toString() {
            return side;
        }
    }

    @Override
    public String toString() {
        UriFormatter uri = new UriFormatter();
        uri.addToUri("symbol", getSymbol());
        uri.addToUri("side", getSide().toString());
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
        this.side = amount < 0 ? OrderSide.Sell : OrderSide.Buy;
        this.price = price;
        this.quantity = Math.abs(amount);
    }
}
