package com.bank.transaction.services;

import com.bank.transaction.models.utils.ResponseService;
import reactor.core.publisher.Mono;


public interface ActiveService {

    Mono<ResponseService> findByCode(String id);
}
