package com.bank.transaction.services;

import com.bank.transaction.models.utils.ResponseActive;
import reactor.core.publisher.Mono;


public interface ActiveService {

    Mono<ResponseActive> findByCode(String id);
}
