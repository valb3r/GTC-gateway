package com.gtc.model.tradinggateway.api.dto.command.account;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.*;

/**
 * Created by Valentyn Berezin on 03.03.18.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class GetAllBalancesCommand extends AbstractMessage {

    private static final String HEADER = "getAllBalances";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    @Builder
    public GetAllBalancesCommand(String clientName, String id) {
        super(clientName, id);
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
