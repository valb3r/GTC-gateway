package com.gtc.tradinggateway.util;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by mikro on 15.02.2018.
 */
public class BasicHttpAuthHelper {

    public static String generateToken(String username, String password) {
        String key = username + ":" + password;
        return Base64.encodeBase64String(key.getBytes());
    }

}
