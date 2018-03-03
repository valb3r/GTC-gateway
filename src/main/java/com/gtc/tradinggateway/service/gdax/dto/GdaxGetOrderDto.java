package com.gtc.tradinggateway.service.gdax.dto;

import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import lombok.Data;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
@Data
public class GdaxGetOrderDto {

    private String id;
    private double size;
    private double price;
    private String status;

    public OrderDto map() {
        throw new IllegalStateException("Unimplemented");
    }
}
