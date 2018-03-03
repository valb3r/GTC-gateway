package com.gtc.model.tradinggateway.api.dto.command.account;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
