package com.gtc.tradinggateway.util;

import com.gtc.tradinggateway.meta.PairSymbol;
import lombok.experimental.UtilityClass;

/**
 * Created by Valentyn Berezin on 05.03.18.
 */
@UtilityClass
public class DefaultInvertHandler {

    private static final String SELL = "sell";
    private static final String BUY = "buy";

    public double amount(PairSymbol symbol, double amount, double price) {
        if (symbol.getIsInverted()) {
            return -amount * price;
        }

        return amount;
    }

    public double price(PairSymbol symbol, double price) {
        if (symbol.getIsInverted()) {
            return 1.0 / price;
        }

        return price;
    }

    public String amountToBuyOrSell(double amount) {
        return amount < 0 ? SELL : BUY;
    }
}
