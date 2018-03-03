package com.gtc.model.tradinggateway.api.dto.command.manage;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.*;

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

    @Builder
    public ListOpenCommand(String clientName, String id) {
        super(clientName, id);
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
