package com.bank.transaction.services;

import com.bank.transaction.Mont;
import com.bank.transaction.ResponseMont;
import com.bank.transaction.models.utils.ResponseActive;
import reactor.core.publisher.Mono;

public interface ActiveService {
    Mono<ResponseActive> findByCode(String id);

    Mono<ResponseMont> getMont(String idActive, String idCredit);
    Mono<ResponseMont> setMont(String idActive, String idCredit, Mont mont);
}
