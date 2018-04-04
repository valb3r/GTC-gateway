package com.gtc.tradinggateway.service.huobi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gtc.tradinggateway.service.huobi.HuobiEncryptionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@JsonPropertyOrder({"AccessKeyId", "SignatureMethod", "SignatureVersion", "Timestamp", "states"})
public class HuobiRequestDto {

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Timestamp = dateFormat.format(new Date());
    }
}
