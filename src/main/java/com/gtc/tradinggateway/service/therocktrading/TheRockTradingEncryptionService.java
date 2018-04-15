package com.gtc.tradinggateway.service.therocktrading;

import com.google.common.collect.ImmutableMap;
import com.gtc.tradinggateway.config.TheRockTradingConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
public class TheRockTradingEncryptionService {

    private static final String METHOD = "HmacSHA512";

    private final TheRockTradingConfig cfg;

    @SneakyThrows
    private String generate(String absUrl, String nonce) {
        String preHash = nonce + absUrl;
        byte[] secretDecoded = Base64.getDecoder().decode(cfg.getSecretKey());
        SecretKeySpec keySpec = new SecretKeySpec(secretDecoded, METHOD);
        Mac mac = (Mac) Mac.getInstance(METHOD).clone();
        mac.init(keySpec);
        return String.format("%0128x", new BigInteger(1, mac.doFinal(preHash.getBytes())));

    }

    private Map<String, String> signingHeaders(String absUrl) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());

        return ImmutableMap.<String, String>builder()
                .put("Content-Type", APPLICATION_JSON.toString())
                .put("X-TRT-KEY", cfg.getPublicKey())
                .put("X-TRT-SIGN", generate(absUrl, timestamp))
                .put("X-TRT-NONCE", timestamp)
                .build();
    }

    public HttpHeaders restHeaders(String absUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(signingHeaders(absUrl));
        return headers;
    }

}
