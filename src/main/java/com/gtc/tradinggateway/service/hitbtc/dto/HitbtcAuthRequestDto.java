package com.gtc.tradinggateway.service.hitbtc.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created by mikro on 15.02.2018.
 */
@Data
public class HitbtcAuthRequestDto {

    private String method = "login";
    private AuthBody params;

    @Data
    @RequiredArgsConstructor
    public static class AuthBody {

        private String algo = "BASIC";
        private final String pKey;
        private final String sKey;

        public String getpKey() {
            return pKey;
        }

        public String getsKey() {
            return sKey;
        }
    }

    public HitbtcAuthRequestDto(String pKey, String sKey) {
        params = new AuthBody(pKey, sKey);
    }

}
