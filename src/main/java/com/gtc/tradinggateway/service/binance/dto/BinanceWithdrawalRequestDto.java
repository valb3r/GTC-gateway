package com.gtc.tradinggateway.service.binance.dto;

import com.gtc.tradinggateway.util.UriFormatter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by mikro on 28.01.2018.
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BinanceWithdrawalRequestDto extends BinanceRequestDto {

    private final String asset;
    private final BigDecimal amount;
    private final String address;

    @Override
    public String toString() {
        UriFormatter uri = new UriFormatter();
        uri.addToUri("asset", getAsset());
        uri.addToUri("address", getAddress());
        uri.addToUri("amountFromOrig", String.valueOf(getAmount()));
        uri.addToUri("timestamp", String.valueOf(getTimestamp()));
        uri.addToUri("recvWindow", String.valueOf(getRecvWindow()));
        return uri.toString();
    }
}
