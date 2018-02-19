package com.gtc.tradinggateway.service.hitbtc;

import com.appunite.websocket.rx.object.messages.RxObjectEventConnected;
import com.gtc.tradinggateway.config.HitbtcConfig;
import com.gtc.tradinggateway.service.CreateOrder;
import org.springframework.stereotype.Service;

/**
 * Created by mikro on 19.02.2018.
 */
@Service
public class HitbtcWsServiceTestable extends HitbtcWsService implements CreateOrder {

    public HitbtcWsServiceTestable(HitbtcConfig cfg) {
        super(cfg);
    }

    public void setRxConnected(RxObjectEventConnected value) {
        rxConnected.set(value);
    }

    public void setIsLoggedIn(Boolean value) {
        isLoggedIn.set(value);
    }

}
