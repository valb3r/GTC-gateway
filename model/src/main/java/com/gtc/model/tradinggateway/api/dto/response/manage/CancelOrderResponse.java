package com.gtc.model.tradinggateway.api.dto.response.manage;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import com.gtc.model.tradinggateway.api.dto.WithOrderId;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class CancelOrderResponse extends AbstractMessage implements WithOrderId {

    private static final String HEADER = "resp.cancel";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    @NotBlank
    private String orderId;

    @Builder
    public CancelOrderResponse(String clientName, String id, String orderId) {
        super(clientName, id);
        this.orderId = orderId;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
