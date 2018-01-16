package com.gtc.tradinggateway.service.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
@Data
@Builder
public class OrderDto {

    private String id;
    private String size;
    private String price;
    private String status;
}
