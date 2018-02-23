package com.gtc.model.tradinggateway.api.dto.command.create;

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
public class CreateOrderCommand extends AbstractMessage {

    private static final String HEADER = "create";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    @NotBlank
    private final String currencyFrom;

    @NotBlank
    private final String currencyTo;

    @DecimalMin(MIN_DECIMAL)
    private final BigDecimal price;

    @DecimalMin(MIN_DECIMAL)
    private final BigDecimal amount;

    @Builder
    public CreateOrderCommand(String clientName, String id, String currencyFrom, String currencyTo, double price,
                              double amount) {
        super(clientName, id);
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.price = BigDecimal.valueOf(price);
        this.amount = BigDecimal.valueOf(amount);
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
