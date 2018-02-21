package com.gtc.model.tradinggateway.api.dto.command.manage;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.Builder;
import lombok.ToString;

/**
 * Created by Valentyn Berezin on 21.02.18.
 */
@ToString
public class ListOpenCommand extends AbstractMessage {

    private static final String HEADER = "list";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    @Builder
    public ListOpenCommand(String clientName, String id) {
        super(clientName, id);
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
