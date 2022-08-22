package com.bank.transaction.services;

import com.bank.transaction.models.utils.Mont;
import com.bank.transaction.models.utils.ResponseMont;
import com.bank.transaction.models.utils.ResponseActive;
import reactor.core.publisher.Mono;

public interface ActiveService {
    Mono<ResponseActive> findType(String id);
    Mono<ResponseMont> getMont(String idActive, String idCredit);
}
