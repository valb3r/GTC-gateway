package com.gtc.model.tradinggateway.api.dto.data;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
@Data
@Builder
public class OrderDto implements Serializable {

    private String id;
    private double size;
    private double price;
    private OrderStatus status;
    private String statusString;
}
