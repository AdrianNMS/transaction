package com.bank.transaction.services.impl;

import com.bank.transaction.models.utils.Mont;
import com.bank.transaction.models.utils.ResponseMont;
import com.bank.transaction.models.utils.ResponseActive;
import com.bank.transaction.services.ActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class ActiveImpl implements ActiveService {

    @Qualifier("getWebClientActive")
    @Autowired
    WebClient webClient;

    @Override
    public Mono<ResponseActive> findType(String id)
    {
        return webClient.get()
                .uri("/api/active/type/"+ id)
                .retrieve()
                .bodyToMono(ResponseActive.class);
    }

    @Override
    public Mono<ResponseMont> getMont(String idActive, String idCredit) {
        return webClient.get()
                .uri("/api/active/mont/"+ idActive+"/"+idCredit)
                .retrieve()
                .bodyToMono(ResponseMont.class);
    }

}
