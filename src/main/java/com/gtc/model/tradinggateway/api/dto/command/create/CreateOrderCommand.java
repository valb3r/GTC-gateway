package com.gtc.model.tradinggateway.api.dto.command.create;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
@ToString
public class CreateOrderCommand extends AbstractMessage {

    private static final String HEADER = "create";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    private final String currencyFrom;
    private final String currencyTo;
    private final double price;
    private final double amount;

    @Builder
    public CreateOrderCommand(String clientName, String id, String currencyFrom, String currencyTo, double price,
                              double amount) {
        super(clientName, id);
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.price = price;
        this.amount = amount;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
