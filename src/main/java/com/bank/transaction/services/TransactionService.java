package com.bank.transaction.services;

import com.bank.transaction.models.documents.Transaction;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TransactionService
{
    Mono<List<Transaction>> findAll();
    Mono<Transaction> find(String id);
    Mono<Transaction> create(Transaction tran);
    Mono<Transaction> update(String id, Transaction tran);
    Mono<Object> delete(String id);
    Mono<List<Transaction>> findByIdClient(String id);
    Mono<Float> getBalance(String id, String idCredit);
    Mono<Float> getDebtMonth(String id, String idCredit);
}
