package com.gtc.tradinggateway.service;

import com.gtc.tradinggateway.service.dto.OrderDto;

import java.util.List;
import java.util.Optional;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
public interface ManageOrders {

    Optional<OrderDto> get(String id);

    List<OrderDto> getOpen();

    void cancel(String id);
}
