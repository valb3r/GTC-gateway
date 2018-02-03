package com.gtc.tradinggateway.service.binance;

import com.gtc.tradinggateway.config.BinanceConfig;
import com.gtc.tradinggateway.service.PairService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by mikro on 02.02.2018.
 */
@Service
@Slf4j
public class BinancePairService extends PairService {

    protected final BinanceConfig cfg;

    public BinancePairService(BinanceConfig cfg) {
        this.cfg = cfg;
        parse(cfg.getPairs());
        log.info(pairsMap.toString());
    }

}
