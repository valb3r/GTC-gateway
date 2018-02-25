package com.gtc.model.tradinggateway.api.dto.command.create;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateOrderCommand extends AbstractMessage {

    private static final String HEADER = "create";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    @NotBlank
    private String currencyFrom;

    @NotBlank
    private String currencyTo;

    @DecimalMin(MIN_DECIMAL)
    private BigDecimal price;

    @NotNull
    private BigDecimal amount;

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
