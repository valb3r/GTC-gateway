package com.gtc.tradinggateway.service.wex.dto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.model.tradinggateway.api.dto.data.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Valentyn Berezin on 05.03.18.
 */
@Getter
@Setter
@NoArgsConstructor
public class WexGetResponse extends BaseWexResponse<Map<String, WexGetResponse.Value>> {

    public static final String SELL = "sell";

    private static final Map<Integer, OrderStatus> MAPPER = ImmutableMap.<Integer, OrderStatus>builder()
            .put(0, OrderStatus.PARTIALLY_FILLED)
            .put(1, OrderStatus.FILLED)
            .put(2, OrderStatus.CANCELED)
            .put(3, OrderStatus.CANCELED)
            .build();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Value {

        private String pair;
        private String type;
        private double startAmount;
        private double amount;
        private double rate;
        private long timestampCreated;
        private int status;
    }

    public Optional<OrderDto> mapTo () {
        String id = Iterables.getFirst(getRet().keySet(), "");
        Value value = getRet().get(id);

        return mapTo(id, value);
    }

    static Optional<OrderDto> mapTo (String id, Value value) {
        if (null == value) {
            return Optional.empty();
        }

        return Optional.of(OrderDto.builder()
                .orderId(id)
                .size(SELL.equals(value.getType()) ? -value.getAmount() : value.getAmount())
                .price(value.getRate())
                .status(MAPPER.getOrDefault(value.getStatus(), OrderStatus.UNMAPPED))
                .statusString(String.valueOf(value.getStatus()))
                .build());
    }
}
