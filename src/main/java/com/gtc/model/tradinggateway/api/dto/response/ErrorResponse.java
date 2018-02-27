package com.gtc.model.tradinggateway.api.dto.response;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Valentyn Berezin on 27.02.18.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ErrorResponse extends AbstractMessage {

    private static final String HEADER = "resp.error";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    @NotBlank
    private String onMessageId;

    @NotBlank
    private String errorCause;

    private boolean isTransient;

    @NotBlank
    private String occurredOn;

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
