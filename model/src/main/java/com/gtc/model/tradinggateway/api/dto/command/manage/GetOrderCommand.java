package com.gtc.model.tradinggateway.api.dto.command.manage;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
@Setter
@NoArgsConstructor
public class GetOrderCommand extends AbstractMessage {

    private static final String HEADER = "get";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    @NotBlank
    private String orderId;

    @Builder
    public GetOrderCommand(String clientName, String id, String orderId) {
        super(clientName, id);
        this.orderId = orderId;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
