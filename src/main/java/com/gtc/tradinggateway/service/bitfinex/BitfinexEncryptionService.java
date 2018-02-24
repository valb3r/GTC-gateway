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

    public Map<String, String> signingHeaders(Object request) {
        String payload = generatePayload(request);
        return ImmutableMap.<String, String>builder()
                .put("accept", APPLICATION_JSON.toString())
                .put("content-type", APPLICATION_JSON.toString())
                .put("X-BFX-APIKEY", cfg.getPublicKey())
                .put("X-BFX-PAYLOAD", payload)
                .put("X-BFX-SIGNATURE",
                        generateSignature(payload, cfg.getSecretKey()).replace("-", "").toLowerCase())
                .build();
    }

    @SneakyThrows
    private String generatePayload(Object request) {
        String payload = cfg.getMapper().writeValueAsString(request);
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }

    @SneakyThrows
    public String generateSignature(String msg, String keyString) {
        String digest;
        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), METHOD);
        Mac mac = Mac.getInstance(METHOD);
        mac.init(key);

        byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));

        StringBuffer hash = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                hash.append('0');
            }
            hash.append(hex);
        }
        digest = hash.toString();
        return digest;
    }

    public HttpHeaders restHeaders(Object request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(signingHeaders(request));
        return headers;
    }
}
