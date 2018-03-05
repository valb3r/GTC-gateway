package com.gtc.tradinggateway.service.binance.dto;

import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.util.DefaultInvertHandler;
import com.gtc.tradinggateway.util.UriFormatter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Created by mikro on 01.02.2018.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BinancePlaceOrderRequestDto extends BinanceRequestDto {

    private PairSymbol symbol;

    private String side;

    private String type = "LIMIT";

    private BigDecimal quantity;

    private BigDecimal price;

    private String timeInForce = "GTC";

    @Override
    public String toString() {
        UriFormatter uri = new UriFormatter();
        uri.addToUri("symbol", getSymbol());
        uri.addToUri("side", getSide());
        uri.addToUri("type", getType());
        uri.addToUri("timeInForce", getTimeInForce());
        uri.addToUri("quantity", String.valueOf(getQuantity()));
        uri.addToUri("priceFromOrig", String.valueOf(getPrice()));
        uri.addToUri("recvWindow", String.valueOf(getRecvWindow()));
        uri.addToUri("timestamp", String.valueOf(getTimestamp()));
        return uri.toString();
    }

    public BinancePlaceOrderRequestDto (PairSymbol symbol, BigDecimal amount, BigDecimal price) {
        this.symbol = symbol;
        this.side = DefaultInvertHandler.amountToBuyOrSellUpper(amount);
        this.price = price;
        this.quantity = amount.abs();
    }
}
