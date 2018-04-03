package com.gtc.tradinggateway.service.huobi;

import com.gtc.tradinggateway.config.HuobiConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Service
@RequiredArgsConstructor
public class HuobiEncryptionService {

    public static final String METHOD = "HmacSHA256";

    private final HuobiConfig cfg;

    @SneakyThrows
    public String generate(HttpMethod method, String url, String params) {
        String message = method.name() + "\napi.huobi.pro\n" + url + "\n" + params;
        String secret = cfg.getSecretKey();
        Mac sha256hmac = Mac.getInstance(METHOD);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), METHOD);
        sha256hmac.init(secretKeySpec);
        return new String(Hex.encodeHex(sha256hmac.doFinal(message.getBytes())));
    }

    public HttpHeaders restHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }
}
