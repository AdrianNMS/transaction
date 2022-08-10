package com.bank.transaction.services.impl;

import com.bank.transaction.models.utils.ResponseService;
import com.bank.transaction.services.ActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class ActiveImpl implements ActiveService {

    @Autowired
    WebClient webClient;

    @Override
    public Mono<ResponseService> findByCode(String id)
    {
        return webClient.get()
                .uri("/api/active/"+ id)
                .retrieve()
                .bodyToMono(ResponseService.class);
    }

}
