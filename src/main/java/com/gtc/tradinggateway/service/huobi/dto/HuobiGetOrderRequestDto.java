package com.gtc.tradinggateway.service.huobi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"AccessKeyId", "SignatureMethod", "SignatureVersion", "Timestamp", "orderId"})
public class HuobiGetOrderRequestDto extends HuobiRequestDto {

    @JsonProperty("order-id")
    private final String orderId;

    public HuobiGetOrderRequestDto(String accessKey, String orderId) {
        super(accessKey);
        this.orderId = orderId;
    }
}
