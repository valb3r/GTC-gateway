package com.gtc.tradinggateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
@Data
public class BaseConfig {

    protected String wsBase;
    protected String restBase;
    protected String passphrase;
    protected String publicKey;
    protected String secretKey;
    protected List<String> pairs;

    protected ObjectMapper mapper = new ObjectMapper();

    protected RestTemplate restTemplate = new RestTemplate();

    protected int disconnectIfInactiveS = 10;
}
