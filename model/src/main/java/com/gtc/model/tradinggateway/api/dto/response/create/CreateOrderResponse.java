package com.gtc.model.tradinggateway.api.dto.response.create;

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
public class CreateOrderResponse extends AbstractMessage {

    private static final String HEADER = "resp.create";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    @NotBlank
    private String requestOrderId;

    // not all exchanges allow to have client-assigned id
    @NotBlank
    private String assignedId;

    // order can be executed immediately on creation
    private boolean isExecuted;

    @Builder
    public CreateOrderResponse(String clientName, String id, String requestOrderId, String assignedId,
                               boolean isExecuted) {
        super(clientName, id);
        this.requestOrderId = requestOrderId;
        this.assignedId = assignedId;
        this.isExecuted = isExecuted;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
