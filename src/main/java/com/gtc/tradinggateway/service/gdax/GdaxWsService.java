package com.gtc.tradinggateway.service.gdax;

import com.appunite.websocket.rx.object.messages.RxObjectEventConnected;
import com.fasterxml.jackson.databind.JsonNode;
import com.gtc.tradinggateway.config.GdaxConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.BaseWsClient;
import com.gtc.tradinggateway.service.CreateOrder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
@Service
public class GdaxWsService extends BaseWsClient implements CreateOrder {

    private static final String CHANNEL = "user";

    private final GdaxConfig cfg;
    private final GdaxEncryptionService signer;

    public GdaxWsService(GdaxConfig cfg, GdaxEncryptionService signer) {
        super(cfg.getMapper());
        this.cfg = cfg;
        this.signer = signer;
    }

    @Override
    public String create(TradingCurrency currency, double amount, double price) {
        return null;
    }

    @Override
    protected String getWs() {
        return cfg.getWsBase();
    }

    @Override
    protected String name() {
        return null;
    }

    @Override
    protected void onConnected(RxObjectEventConnected conn) {
        // NOP
    }

    @Override
    protected void parseEventDto(JsonNode node) {
        // NOP
    }

    @Override
    protected void parseArray(JsonNode node) {
        // NOP
    }

    @Override
    protected Map<String, String> headers() {
        return signer.signingHeaders("", "", "");
    }

    @Override
    protected int getDisconnectIfInactiveS() {
        return cfg.getDisconnectIfInactiveS();
    }
}
