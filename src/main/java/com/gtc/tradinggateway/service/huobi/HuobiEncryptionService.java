package com.gtc.tradinggateway.service.huobi;

import com.gtc.tradinggateway.config.HuobiConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

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
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), METHOD);
        sha256hmac.init(secretKeySpec);
        return Base64.encodeBase64String(sha256hmac.doFinal(message.getBytes()));
    }

    public HttpHeaders restHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0)");
        return headers;
    }
}
