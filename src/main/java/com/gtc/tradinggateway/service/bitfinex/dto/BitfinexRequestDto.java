package com.gtc.tradinggateway.service.bitfinex.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Created by mikro on 18.02.2018.
 */
@Data
@RequiredArgsConstructor
public class BitfinexRequestDto {

    protected String nonce = UUID.randomUUID().toString();
    protected final String url;

}
