package com.gtc.tradinggateway.service;

import com.gtc.tradinggateway.config.ClientsConf;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeclaredClientsProvider {

    @Getter
    private final List<? extends BaseWsClient> clientList;

    public DeclaredClientsProvider(List<? extends BaseWsClient> clients, ClientsConf conf) {
        this.clientList = clients.stream()
                .filter(client -> conf.getActive().contains(client.name()))
                .collect(Collectors.toList());
    }
}
