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
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public Optional<OrderCreatedDto> create(String tryToAssignId, TradingCurrency from, TradingCurrency to, BigDecimal amount, BigDecimal price) {
        Optional<PairSymbol> pair = cfg.pairFromCurrency(from, to);
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
            BigDecimal reverse = new BigDecimal(1);
            amount = reverse.divide(amount);
            price = reverse.divide(price);
        }

        ObjectWebSocketSender sender = rxConnected.get().sender();
        BitfinexCreateOrderWsDto requestDto =
                new BitfinexCreateOrderWsDto(tryToAssignId, pair.toString(), amount, price);

        RxMoreObservables
                .sendObjectMessage(sender, requestDto)
                .subscribe();
        return Optional.empty();
    }
}
