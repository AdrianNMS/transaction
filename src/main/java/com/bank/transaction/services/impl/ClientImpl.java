package com.bank.transaction.services.impl;

import com.bank.transaction.models.utils.ResponseActive;
import com.bank.transaction.models.utils.ResponseClient;
import com.bank.transaction.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ClientImpl implements ClientService {

    @Qualifier("getWebClientC")
    @Autowired
    WebClient webClient;

    @Override
    public Mono<ResponseClient> findByCode(String id)
    {
        return webClient.get()
                .uri("/api/Client/"+ id)
                .retrieve()
                .bodyToMono(ResponseClient.class);
    }
}
