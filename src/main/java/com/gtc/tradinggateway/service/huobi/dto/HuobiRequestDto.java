package com.gtc.tradinggateway.service.huobi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gtc.tradinggateway.service.huobi.HuobiEncryptionService;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Data
@JsonPropertyOrder({"AccessKeyId", "SignatureMethod", "SignatureVersion", "Timestamp"})
public class HuobiRequestDto {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    private final String AccessKeyId;

    private String SignatureMethod = HuobiEncryptionService.METHOD;

    private String SignatureVersion = "2";

    private String Timestamp;

    public HuobiRequestDto(String AccessKeyId) {
        this.AccessKeyId = AccessKeyId;
        Timestamp = FORMATTER.format(LocalDateTime.now(ZoneOffset.UTC));
    }
}
