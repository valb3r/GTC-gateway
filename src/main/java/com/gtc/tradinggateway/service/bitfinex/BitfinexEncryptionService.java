package com.gtc.tradinggateway.service.bitfinex;

import com.google.common.collect.ImmutableMap;
import com.gtc.tradinggateway.config.BitfinexConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Created by mikro on 15.02.2018.
 */
@Service
@RequiredArgsConstructor
public class BitfinexEncryptionService {

    private static final String METHOD = "HmacSHA384";

    private final BitfinexConfig cfg;

    public Map<String, String> signingHeaders(String url, Object request) {
        String nonce = generatePayload(request);
        return ImmutableMap.<String, String>builder()
                .put("accept", APPLICATION_JSON.toString())
                .put("content-type", APPLICATION_JSON.toString())
                .put("bfx-apikey", cfg.getPublicKey())
                .put("bfx-nonce", nonce)
                .put("bfx-signature", generateSignature(nonce, cfg.getSecretKey(), METHOD))
                .build();
    }

    @SneakyThrows
    private String generatePayload(Object request) {
        String payload = cfg.getMapper().writeValueAsString(request);
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }

    @SneakyThrows
    private String generateSignature(String url, String msg, String nonce) {
        String signature = url+ "/api/${msg}${nonce}${rawBody}" + url + nonce + msg;
        return null;
    }

    public HttpHeaders restHeaders(Object request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(signingHeaders(request));
        return headers;
    }

}
