package com.gtc.model.tradinggateway.api.dto.command.create;

import com.gtc.model.tradinggateway.api.dto.AbstractMessage;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Set;

/**
 * Transaction (in terms of receiving)-like creation of multiple commands.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class MultiOrderCreateCommand extends AbstractMessage {

    private static final String HEADER = "createMulti";

    public static final String SELECTOR = HEADER_NAME + "='" + HEADER + "'";

    @NotEmpty
    private Set<CreateOrderCommand> commands;

    @Builder(toBuilder = true)
    public MultiOrderCreateCommand(String clientName, String id, Set<CreateOrderCommand> commands) {
        super(clientName, id);
        this.commands = commands;
    }

    @Override
    protected String getHeader() {
        return HEADER;
    }
}
