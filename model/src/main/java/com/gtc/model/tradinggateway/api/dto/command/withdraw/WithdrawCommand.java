package com.gtc.model.tradinggateway.api.dto.command.withdraw;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
@ToString
public class WithdrawCommand extends AbstractMessage {

    private static final String HEADER = "withdraw";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    private final String currency;
    private final double amount;
    private final String toDestination;

    @Builder
    public WithdrawCommand(String clientName, String id, String currency, double amount, String toDestination) {
        super(clientName, id);
        this.currency = currency;
        this.amount = amount;
        this.toDestination = toDestination;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
