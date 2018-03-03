package com.gtc.model.tradinggateway.api.dto.response.account;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.*;

import java.util.Map;

/**
 * Created by Valentyn Berezin on 03.03.18.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class GetAllBalancesResponse extends AbstractMessage {

    private static final String HEADER = "resp.getAllBalances";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    private Map<String, Double> balances;

    @Override
    protected String getHeader() {
        return HEADER;
    }

    @Builder
    public GetAllBalancesResponse(String clientName, String id, Map<String, Double> balances) {
        super(clientName, id);
        this.balances = balances;
    }
}
