package com.bank.transaction.services.impl;

import com.bank.transaction.models.dao.TransactionDao;
import com.bank.transaction.models.documents.Transaction;
import com.bank.transaction.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionImpl implements TransactionService
{
    @Autowired
    private TransactionDao dao;

    @Override
    public Mono<List<Transaction>> findAll() {
        return dao.findAll()
                .collectList();
    }

    @Override
    public Mono<Transaction> find(String id) {
        return dao.findById(id);
    }

    @Override
    public Mono<Transaction> create(Transaction tran) {
        return dao.save(tran);
    }

    @Override
    public Mono<Transaction> update(String id, Transaction tran) {
        return dao.existsById(id).flatMap(check -> {
            if (check)
            {
                tran.setDateUpdate(LocalDateTime.now());
                return dao.save(tran);
            }
            else
                return Mono.empty();
        });
    }

    @Override
    public Mono<Object> delete(String id) {
        return dao.existsById(id).flatMap(check -> {
            if (check)
                return dao.deleteById(id).then(Mono.just(true));
            else
                return Mono.empty();
        });
    }

    @Override
    public Mono<List<Transaction>> findByIdClient(String id) {
        return null;
    }

    @Override
    public Mono<Float> getBalance(String id, String idCredit) {
        LocalDateTime dateNow = LocalDateTime.now();
        return findAll()
                .flatMap(transactions ->
                        Mono.just((float)transactions.stream()
                                .filter(transaction ->
                                        transaction.getDateRegister().getMonthValue() == dateNow.getMonthValue()
                                                && transaction.getDateRegister().getYear() == dateNow.getYear() &&
                                                (transaction.getActiveId().equals(id) && transaction.getCreditId().equals(idCredit))
                                )
                                .mapToDouble(Transaction::getMont)
                                .sum()
                        ));
    }

    //Deberia ser mensual - diferente
    /*
     * transaction.getDateRegister().getMonthValue() != dateNow.getMonthValue()
     *                                                 && transaction.getDateRegister().getYear() != dateNow.getYear() &&
     *
     */
    @Override
    public Mono<Float> getTotalBalance(String id, String idCredit) {
        LocalDateTime dateNow = LocalDateTime.now();
        return findAll()
                .flatMap(transactions ->
                        Mono.just((float)transactions.stream()
                                .filter(transaction ->
                                        (transaction.getActiveId().equals(id) && transaction.getCreditId().equals(idCredit))
                                )
                                .mapToDouble(Transaction::getMont)
                                .sum()
                        ));
    }
}
