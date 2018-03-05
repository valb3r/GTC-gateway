package com.gtc.tradinggateway.util;

import com.gtc.tradinggateway.meta.PairSymbol;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Valentyn Berezin on 05.03.18.
 */
@UtilityClass
public class DefaultInvertHandler {

    private static final String SELL = "sell";
    private static final String BUY = "buy";

    private static final String SELL_UPPER = "SELL";
    private static final String BUY_UPPER = "BUY";

    public BigDecimal amountFromOrig(PairSymbol symbol, BigDecimal amount, BigDecimal price) {
        if (symbol.getIsInverted()) {
            return amount.negate().multiply(price);
        }

        return amount;
    }

    public BigDecimal priceFromOrig(PairSymbol symbol, BigDecimal price) {
        if (symbol.getIsInverted()) {
            return BigDecimal.ONE.divide(price, RoundingMode.HALF_EVEN);
        }

        return price;
    }

    public String amountToBuyOrSell(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) < 0 ? SELL : BUY;
    }

    public String amountToBuyOrSellUpper(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) < 0 ? SELL_UPPER : BUY_UPPER;
    }

    public BigDecimal mapFromBuyOrSell(String buyOrSell, BigDecimal amount) {
        return SELL.equalsIgnoreCase(buyOrSell) ? amount.negate() : amount;
    }
}
