package com.gtc.model.tradinggateway.api.dto.response.withdraw;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
public class WithdrawOrderResponse extends AbstractMessage {

    private static final String HEADER = "resp.withdraw";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    @NotBlank
    private final String currency;

    private final double amount;

    @NotBlank
    private final String toDestination;

    @Builder
    public WithdrawOrderResponse(String clientName, String id, String currency, double amount, String toDestination) {
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
