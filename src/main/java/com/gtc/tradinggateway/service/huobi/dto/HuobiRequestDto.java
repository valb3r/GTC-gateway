package com.gtc.tradinggateway.service.huobi.dto;

import com.gtc.tradinggateway.service.huobi.HuobiEncryptionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class HuobiRequestDto {

    private final String AccessKeyId;
    private String SignatureMethod = HuobiEncryptionService.METHOD;
    private String SignatureVersion = "2";
    private Long Timestamp = System.currentTimeMillis();
}
