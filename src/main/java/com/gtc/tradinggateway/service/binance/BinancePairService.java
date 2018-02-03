package com.gtc.tradinggateway.service.binance;

import com.gtc.tradinggateway.config.BinanceConfig;
import com.gtc.tradinggateway.service.PairService;
import org.springframework.stereotype.Service;

/**
 * Created by mikro on 02.02.2018.
 */
@Service
public class BinancePairService extends PairService {

    protected final BinanceConfig cfg;

    public BinancePairService(BinanceConfig cfg) {
        this.cfg = cfg;
        parse(cfg.getPairs());
    }

}
