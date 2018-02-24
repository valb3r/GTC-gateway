package com.gtc.tradinggateway.service.bitfinex;

import com.appunite.websocket.rx.RxMoreObservables;
import com.appunite.websocket.rx.object.ObjectWebSocketSender;
import com.appunite.websocket.rx.object.messages.RxObjectEventConnected;
import com.fasterxml.jackson.databind.JsonNode;
import com.gtc.tradinggateway.config.BitfinexConfig;
import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.BaseWsClient;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.bitfinex.dto.BitfinexAuthWsRequestDto;
import com.gtc.tradinggateway.service.bitfinex.dto.BitfinexAuthWsResponseDto;
import com.gtc.tradinggateway.service.bitfinex.dto.BitfinexCreateOrderWsDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.gtc.tradinggateway.config.Const.Clients.BITFINEX;

/**
 * Created by mikro on 20.02.2018.
 */
@Slf4j
@Service
@EnableScheduling
public class BitfinexWsClient extends BaseWsClient implements CreateOrder {

    private static String EVENT_ALIAS = "event";
    private static String AUTH_EVENT = "auth";
    private static String NEW_ORDER_EVENT = "on";
    private static String SUCCESS_STATUS = "OK";

    private final BitfinexConfig cfg;
    private final BitfinexEncryptionService signer;

    public BitfinexWsClient(BitfinexConfig cfg, BitfinexEncryptionService signer) {
        super(cfg.getMapper());
        this.cfg = cfg;
        this.signer = signer;
    }

    protected String getWs() {
        return cfg.getWsBase();
    }

    protected Map<String, String> headers() {
        return new HashMap<>();
    }

    protected void onConnected(RxObjectEventConnected conn) {
        return;
    }

    @SneakyThrows
    protected void parseEventDto(JsonNode node) {
        if (AUTH_EVENT.equals(node.get(EVENT_ALIAS).asText())) {
            BitfinexAuthWsResponseDto authEvent = objectMapper.readerFor(BitfinexAuthWsResponseDto.class)
                    .readValue(node);
            if (SUCCESS_STATUS.equals(authEvent.getStatus())) {
                isLoggedIn.set(true);
            } else {
                isLoggedIn.set(false);
                throw new AuthException();
            }
        }
    }

    @SneakyThrows
    protected void parseArray(JsonNode node) {
        if (NEW_ORDER_EVENT.equals(node.get(1).asText())) {
            BitfinexCreateOrderWsDto dto = objectMapper.readerFor(BitfinexCreateOrderWsDto.class)
                    .readValue(node.get(3));
            // TODO: order created event
        }
    }

    protected int getDisconnectIfInactiveS() {
        return cfg.getDisconnectIfInactiveS();
    }

    protected void login() {
        ObjectWebSocketSender sender = rxConnected.get().sender();
        String nonce = String.valueOf(System.currentTimeMillis());
        String message = "AUTH" + nonce;
        BitfinexAuthWsRequestDto requestDto = new BitfinexAuthWsRequestDto(
                cfg.getPublicKey(),
                signer.generateSignature(message, cfg.getSecretKey()),
                message,
                nonce);
        RxMoreObservables
                .sendObjectMessage(sender, requestDto)
                .subscribe();

    }

    public String name() {
        return BITFINEX;
    }

    public String create(TradingCurrency from, TradingCurrency to, double amount, double price) {
        Optional<PairSymbol> pair = cfg.fromCurrency(from, to);
        if (isDisconnected() || !isLoggedIn.get()) {
            throw new IllegalStateException(
                    "Failed request. Connect status: " + !isDisconnected() + ", Login status: " + !isLoggedIn.get());
        }

        if (!pair.isPresent()) {
            throw new IllegalArgumentException(
                    "Pair from " + from.toString() + " to " + to.toString() + " is not supported");
        }
        PairSymbol pairSym = pair.get();
        if (pairSym.getIsInverted()) {
            amount = 1 / amount;
            price = 1 / price;
        }

        String id = UUID.randomUUID().toString();

        ObjectWebSocketSender sender = rxConnected.get().sender();
        BitfinexCreateOrderWsDto requestDto =
                new BitfinexCreateOrderWsDto(id, pair.toString(), amount, price);

        RxMoreObservables
                .sendObjectMessage(sender, requestDto)
                .subscribe();
        return id;
    }
}
