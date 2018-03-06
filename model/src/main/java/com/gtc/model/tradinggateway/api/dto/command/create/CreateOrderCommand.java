package com.gtc.model.tradinggateway.api.dto.command.create;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import com.gtc.model.tradinggateway.api.dto.WithOrderId;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class CreateOrderCommand extends AbstractMessage implements WithOrderId {

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

    @NotNull
    private String orderId; // it is not guaranteed we will get it assigned

    @Builder
    public CreateOrderCommand(String clientName, String id, String currencyFrom, String currencyTo,
                              BigDecimal price, BigDecimal amount, String orderId) {
        super(clientName, id);
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.price = price;
        this.amount = amount;
        this.orderId = orderId;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
