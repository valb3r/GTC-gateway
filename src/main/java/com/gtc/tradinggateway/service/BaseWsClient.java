package com.gtc.tradinggateway.service;

import com.appunite.websocket.rx.RxWebSockets;
import com.appunite.websocket.rx.object.ObjectSerializer;
import com.appunite.websocket.rx.object.RxObjectWebSockets;
import com.appunite.websocket.rx.object.messages.RxObjectEvent;
import com.appunite.websocket.rx.object.messages.RxObjectEventConnected;
import com.appunite.websocket.rx.object.messages.RxObjectEventMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.gtc.tradinggateway.service.rxsupport.JacksonSerializer;
import com.gtc.tradinggateway.service.rxsupport.MoreObservables;
import com.newrelic.api.agent.NewRelic;
import lombok.SneakyThrows;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
public abstract class BaseWsClient {

    private static final String CONNECTS = "Custom/Connect/";
    private static final String MESSAGE = "Custom/Message/";

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final ObjectMapper objectMapper;
    protected final AtomicReference<RxObjectEventConnected> rxConnected = new AtomicReference<>();
    protected final AtomicReference<Boolean> isLoggedIn = new AtomicReference<>();

    public BaseWsClient(ObjectMapper mapper) {
        this.objectMapper = mapper;
    }

    @SneakyThrows
    public void connect() {
        if (null != rxConnected.get()) {
            return;
        }

        log.info("Connecting");
        Request request = new Request.Builder()
                .get()
                .headers(Headers.of(headers()))
                .url(getWs())
                .build();

        Observable<RxObjectEvent> sharedConnection = getConnection(request);

        sharedConnection
                .compose(MoreObservables.filterAndMap(RxObjectEventConnected.class))
                .subscribe(onConn -> {
                    NewRelic.incrementCounter(CONNECTS + name());
                    rxConnected.set(onConn);
                    log.info("Connected");
                    onConnected(onConn);
                });

        sharedConnection
                .compose(MoreObservables.filterAndMap(RxObjectEventMessage.class))
                .compose(RxObjectEventMessage.filterAndMap(JsonNode.class))
                .subscribe(this::handleInboundMessage);
    }

    private Observable<RxObjectEvent> getConnection(Request request) {
        ObjectSerializer serializer = new JacksonSerializer(objectMapper);
        return new RxObjectWebSockets(new RxWebSockets(new OkHttpClient(), request), serializer)
                .webSocketObservable()
                .timeout(getDisconnectIfInactiveS(), TimeUnit.SECONDS)
                .doOnCompleted(() -> handleDisconnectEvt("Disconnected (completed)", null))
                .doOnError(throwable -> handleDisconnectEvt("Disconnected (exceptional)", throwable))
                .share();
    }

    public boolean isDisconnected() {
        return null == rxConnected.get();
    }

    protected void handleInboundMessage(JsonNode node) {
        NewRelic.incrementCounter(MESSAGE + name());
        try {
            if (!node.isArray()) {
                parseEventDto(node);
            } else if (node.isArray()) {
                parseArray(node);
            }
        } catch (RuntimeException ex) {
            NewRelic.noticeError(ex, ImmutableMap.of("name", name()));
            log.error("Exception handling {}", node, ex);
        }
    }

    protected abstract String getWs();
    protected abstract String name();
    protected abstract Map<String, String> headers();
    protected abstract void onConnected(RxObjectEventConnected conn);
    protected abstract void parseEventDto(JsonNode node);
    protected abstract void parseArray(JsonNode node);
    protected abstract int getDisconnectIfInactiveS();

    private void handleDisconnectEvt(String reason, Throwable err) {
        rxConnected.set(null);

        if (null != err) {
            NewRelic.noticeError(err, ImmutableMap.of("name", name()));
            log.error(reason, err);
        } else {
            NewRelic.noticeError(name());
            log.error(reason);
        }
    }
}
