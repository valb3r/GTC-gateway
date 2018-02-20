package com.gtc.tradinggateway.service.bitfinex;

import com.appunite.websocket.rx.object.messages.RxObjectEventConnected;
import com.fasterxml.jackson.databind.JsonNode;
import com.gtc.tradinggateway.config.BitfinexConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.BaseWsClient;
import com.gtc.tradinggateway.service.CreateOrder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by mikro on 20.02.2018.
 */
@Service
public class BitfinexWsClient extends BaseWsClient implements CreateOrder {

    private final BitfinexConfig cfg;

    public BitfinexWsClient(BitfinexConfig cfg) {
        super(cfg.getMapper());
        this.cfg = cfg;
    }

    protected String getWs() {
        return null;
    }

    protected Map<String, String> headers() {
        return null;
    }

    protected void onConnected(RxObjectEventConnected conn) {
        return;
    }

    protected void parseEventDto(JsonNode node) {
        return;
    }

    protected void parseArray(JsonNode node) {
        return;
    }

    protected int getDisconnectIfInactiveS() {
        return 1;
    }

    protected void login() {
        return;
    }

    public String name() {
        return null;
    }

    public String create(TradingCurrency from, TradingCurrency to, double amount, double price) {
        return null;
    }

}
