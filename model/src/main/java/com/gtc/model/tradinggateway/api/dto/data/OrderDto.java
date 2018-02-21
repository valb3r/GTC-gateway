package com.gtc.model.tradinggateway.api.dto.data;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
@Data
@Builder
public class OrderDto {

    private String id;
    private double size;
    private double price;
    private String status;
}
