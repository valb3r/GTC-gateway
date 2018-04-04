package com.gtc.tradinggateway.service.huobi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gtc.tradinggateway.service.huobi.HuobiEncryptionService;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Data
@JsonPropertyOrder({"AccessKeyId", "SignatureMethod", "SignatureVersion", "Timestamp", "states"})
public class HuobiRequestDto {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
//    @JsonProperty("AccessKeyId")
    private final String AccessKeyId;

//    @JsonProperty("SignatureMethod")
    private String SignatureMethod = HuobiEncryptionService.METHOD;

//    @JsonProperty("SignatureVersion")
    private String SignatureVersion = "2";

    @JsonProperty("states")
    private String states = "partial-filled,submitted";

    @JsonProperty("symbol")
    private String symbol = "btcusd";

//    @JsonProperty("Timestamp")
    private String Timestamp;

    public HuobiRequestDto(String AccessKeyId) {
        this.AccessKeyId = AccessKeyId;
        Timestamp = FORMATTER.format(LocalDateTime.now(ZoneOffset.UTC));
    }
}
