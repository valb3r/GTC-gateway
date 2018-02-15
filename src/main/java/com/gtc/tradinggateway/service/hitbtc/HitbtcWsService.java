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
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcAuthRequestDto;
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcCreateRequestDto;
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcErrorDto;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.gtc.tradinggateway.config.Const.Clients.HITBTC;

/**
 * Created by mikro on 14.02.2018.
 */
@Service
public class HitbtcWsService extends BaseWsClient implements CreateOrder {

    private static String SELL = "sell";
    private static String BUY = "buy";
    private static String ERROR_ALIAS = "error";
    private static String AUTH_ALIAS = "auth";
    private static String ID_ALIAS = "id";

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
        login();
    }

    @Override
    @SneakyThrows
    protected void parseEventDto(JsonNode node) {
        if (null != node.get(ERROR_ALIAS)) {
            HitbtcErrorDto error = cfg.getMapper().reader().readValue(node.traverse(), HitbtcErrorDto.class);
            isLoggedIn.set(false);
            throw new Exception(error.getError().getMessage());
        } else if (AUTH_ALIAS.equals(node.get(ID_ALIAS).asText())) {
            isLoggedIn.set(true);
        }
    }

    @Override
    protected void parseArray(JsonNode node) {
        // NOP
    }

    @Override
    protected int getDisconnectIfInactiveS() {
        return cfg.getDisconnectIfInactiveS();
    }

    @Override
    protected void login() {
        ObjectWebSocketSender sender = rxConnected.get().sender();
        HitbtcAuthRequestDto requestDto = new HitbtcAuthRequestDto(cfg.getPublicKey(), cfg.getSecretKey());
        RxMoreObservables
                .sendObjectMessage(sender, requestDto)
                .subscribe();
    }

    @SneakyThrows
    public String create(TradingCurrency from, TradingCurrency to, double amount, double price) {
        PairSymbol pair = cfg.fromCurrency(from, to);
        if (isDisconnected() || !isLoggedIn.get() || pair == null) {
            return null;
        }

        if (pair.getIsInverted()) {
            amount = -1 / amount;
            price = 1 / price;
        }

        String side = amount < 0 ? SELL : BUY;

        ObjectWebSocketSender sender = rxConnected.get().sender();
        HitbtcCreateRequestDto requestDto = new HitbtcCreateRequestDto(pair.toString(), side, price, Math.abs(amount));

        RxMoreObservables
                .sendObjectMessage(sender, requestDto)
                .subscribe();
        return requestDto.getParams().getClientOrderId();
    }
}
