package com.gtc.model.tradinggateway.api.dto.command.manage;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class ListOpenCommand extends AbstractMessage {

    private static final String HEADER = "list";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    @NotBlank
    private String currencyFrom;

    @NotBlank
    private String currencyTo;

    @Builder
    public ListOpenCommand(String clientName, String id, String currencyFrom, String currencyTo) {
        super(clientName, id);
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
