package com.gtc.tradinggateway.service.hitbtc;

import com.appunite.websocket.rx.RxMoreObservables;
import com.appunite.websocket.rx.object.ObjectWebSocketSender;
import com.appunite.websocket.rx.object.messages.RxObjectEventConnected;
import com.fasterxml.jackson.databind.JsonNode;
import com.gtc.tradinggateway.config.HitbtcConfig;
import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.BaseWsClient;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcCreateRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.gtc.tradinggateway.config.Const.Clients.HITBTC;

/**
 * Created by mikro on 14.02.2018.
 */
@Slf4j
@Service
@EnableScheduling
public class HitbtcWsService extends BaseWsClient implements CreateOrder {

    private final HitbtcConfig cfg;

    public HitbtcWsService(HitbtcConfig cfg) {
        super(cfg.getMapper());
        this.cfg = cfg;
    }

    @Override
    protected String getWs() {
        return cfg.getWsBase();
    }

    @Override
    protected String name() {
        return HITBTC;
    }

    @Override
    protected Map<String, String> headers() {
        return new HashMap<>();
    }

    @Override
    protected void onConnected(RxObjectEventConnected conn) {
        log.info("Connected");
        create(TradingCurrency.Bitcoin, TradingCurrency.Usd, 0.2, 0.2);
    }

    @Override
    protected void parseEventDto(JsonNode node) {
        log.info(node.toString());
    }

    @Override
    protected void parseArray(JsonNode node) {
        log.info(node.toString());
    }

    @Override
    protected int getDisconnectIfInactiveS() {
        return 1;
    }

    public String create(TradingCurrency from, TradingCurrency to, double amount, double price) {
        if (isDisconnected()) {
            return null;
        }

        PairSymbol pair = cfg.fromCurrency(from, to);
        if (pair == null) {
            return null;
        }
        if (pair.getIsInverted()) {
            amount = -1 / amount;
            price = 1 / price;
        }

        String side = amount < 0 ? "SELL" : "BUY";

        ObjectWebSocketSender sender = rxConnected.get().sender();
        HitbtcCreateRequestDto requestDto = new HitbtcCreateRequestDto(pair.toString(), side, price, Math.abs(amount));

        RxMoreObservables
                .sendObjectMessage(sender, requestDto)
                .subscribe();
        return requestDto.getParams().getClientOrderId();
    }

    @Scheduled(initialDelay = 0, fixedDelay = 1000000)
    public void ttt() {
        connect();
    }

}
