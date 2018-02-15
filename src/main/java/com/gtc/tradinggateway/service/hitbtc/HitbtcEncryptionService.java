package com.gtc.tradinggateway.service.hitbtc;

import com.google.common.collect.ImmutableMap;
import com.gtc.tradinggateway.config.HitbtcConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by mikro on 12.02.2018.
 */
@Service
@RequiredArgsConstructor
public class HitbtcEncryptionService {

    private final HitbtcConfig cfg;

    public Map<String, String> signingHeaders() {
        String key = cfg.getPublicKey() + ":" + cfg.getSecretKey();
        String converted = Base64.encodeBase64String(key.toString().getBytes());

        return ImmutableMap.<String, String>builder()
                .put("Authorization", "Basic " + converted.toString())
                .build();
    }

    public HttpHeaders restHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(signingHeaders());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }
}
