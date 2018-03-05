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
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcAuthRequestDto;
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcCreateRequestDto;
import com.gtc.tradinggateway.service.hitbtc.dto.HitbtcErrorDto;
import com.gtc.tradinggateway.util.DefaultInvertHandler;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.gtc.tradinggateway.config.Const.Clients.HITBTC;

/**
 * Created by mikro on 14.02.2018.
 */
@Service
public class HitbtcWsService extends BaseWsClient implements CreateOrder {

    private static final String ERROR_ALIAS = "error";
    private static final String AUTH_ALIAS = "auth";
    private static final String ID_ALIAS = "id";

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
            throw new IllegalStateException(error.getError().getMessage());
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
    public OrderCreatedDto create(TradingCurrency from, TradingCurrency to, BigDecimal amount, BigDecimal price) {
        Optional<PairSymbol> pair = cfg.pairFromCurrency(from, to);
        if (isDisconnected() || !isLoggedIn.get()) {
            throw new IllegalStateException(
                    "Failed request. Connect status: " + isDisconnected() + ", Login status: " + !isLoggedIn.get());
        }

        if (!pair.isPresent()) {
            throw new IllegalArgumentException(
                    "Pair from " + from.toString() + " to " + to.toString() + " is not supported");
        }
        PairSymbol pairSym = pair.get();

        BigDecimal calcAmount = DefaultInvertHandler.amountFromOrig(pairSym, amount, price);
        BigDecimal calcPrice = DefaultInvertHandler.priceFromOrig(pairSym, price);
        String side = DefaultInvertHandler.amountToBuyOrSell(calcAmount);

        ObjectWebSocketSender sender = rxConnected.get().sender();
        HitbtcCreateRequestDto requestDto =
                new HitbtcCreateRequestDto(pairSym.getSymbol(), side, calcPrice, calcAmount.abs());

        RxMoreObservables
                .sendObjectMessage(sender, requestDto)
                .subscribe();

        return OrderCreatedDto.builder()
                .assignedId(requestDto.getParams().getClientOrderId())
                .build();
    }

    @Override
    public String name() {
        return HITBTC;
    }
}
