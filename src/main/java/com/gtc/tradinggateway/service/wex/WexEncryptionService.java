package com.gtc.tradinggateway.service.wex;

import com.gtc.tradinggateway.config.WexConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

/**
 * Created by Valentyn Berezin on 04.03.18.
 */
@Service
@RequiredArgsConstructor
public class WexEncryptionService {

    private final WexConfig cfg;

    private static final String HMAC_SHA512 = "HmacSHA512";

    @SneakyThrows
    public String getHmacSHA512(String str) {
        Mac macInst;
        String result = "";

        macInst = Mac.getInstance(HMAC_SHA512);
        macInst.init(new SecretKeySpec(cfg.getSecretKey().getBytes(StandardCharsets.UTF_8), HMAC_SHA512));

        result = DatatypeConverter
                .printHexBinary((macInst.doFinal(str.getBytes(StandardCharsets.UTF_8))))
                .toLowerCase();


        return result;
    }
}
