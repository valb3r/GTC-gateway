package com.gtc.model.tradinggateway.api.dto.command.withdraw;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
@ToString
public class WithdrawCommand extends AbstractMessage {

    private static final String HEADER = "withdraw";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    @NotBlank
    private final String currency;

    @DecimalMin(MIN_DECIMAL)
    private final BigDecimal amount;

    @NotBlank
    private final String toDestination;

    @Builder
    public WithdrawCommand(String clientName, String id, String currency, double amount, String toDestination) {
        super(clientName, id);
        this.currency = currency;
        this.amount = BigDecimal.valueOf(amount);
        this.toDestination = toDestination;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
