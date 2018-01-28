package com.gtc.tradinggateway.service.binance;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import com.gtc.tradinggateway.config.BinanceConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Created by mikro on 24.01.2018.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BinanceEncryptionService {

    private static final String METHOD = "HmacSHA256";

    private final BinanceConfig cfg;

    @SneakyThrows
    public String generate(String prehash) {
        log.info(prehash);
        byte[] secretDecoded = Base64.getDecoder().decode(cfg.getSecretKey());
        SecretKeySpec keyspec = new SecretKeySpec(secretDecoded, METHOD);
        Mac sha256 = (Mac) Mac.getInstance(METHOD).clone();
        sha256.init(keyspec);
        return BaseEncoding.base16().lowerCase().encode(sha256.doFinal(prehash.getBytes()));
    }

    public Map<String, String> signingHeaders() {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());

        return ImmutableMap.<String, String>builder()
//                .put("accept", APPLICATION_JSON.toString())
//                .put("content-type", APPLICATION_JSON.toString())
                .put("X-MBX-APIKEY", cfg.getPublicKey())
                .build();
    }

    public HttpHeaders restHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(signingHeaders());
        return headers;
    }

}
