package com.gtc.tradinggateway.meta;

import lombok.Data;

/**
 * Created by mikro on 02.02.2018.
 */
@Data
public class PairSymbol {

    private TradingCurrency from;

    private TradingCurrency to;

    public PairSymbol invert() {
        return new PairSymbol(to, from);
    }

    public PairSymbol(TradingCurrency from, TradingCurrency to) {
        this.from = from;
        this.to = to;
    }

}
