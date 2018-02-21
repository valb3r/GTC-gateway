package com.gtc.tradinggateway.service;

import com.gtc.model.tradinggateway.api.dto.data.OrderDto;

import java.util.List;
import java.util.Optional;

/**
 * Created by Valentyn Berezin on 16.01.18.
 */
public interface ManageOrders extends ClientNamed {

    Optional<OrderDto> get(String id);

    List<OrderDto> getOpen();

    void cancel(String id);
}
