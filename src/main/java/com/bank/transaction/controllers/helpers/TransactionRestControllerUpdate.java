package com.bank.transaction.controllers.helpers;

import com.bank.transaction.handler.ResponseHandler;
import com.bank.transaction.models.documents.Transaction;
import com.bank.transaction.models.utils.Mont;
import com.bank.transaction.services.ActiveService;
import com.bank.transaction.services.TransactionService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class TransactionRestControllerUpdate
{
    public static Mono<ResponseEntity<Object>> createPayment(String id, Transaction tran, Logger log, TransactionService transactionService)
    {
        tran.setDateRegister(LocalDateTime.now());

        return transactionService.update(id, tran)
                .doOnNext(transaction -> log.info(transaction.toString()))
                .flatMap(transaction -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, transaction)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
    }

    //borrar
    public static Mono<ResponseEntity<Object>> CheckDebt(String id, Transaction tran, Logger log, TransactionService transactionService, Mont mont, Transaction oldTransaction)
    {
        return transactionService.getBalance(tran.getActiveId(), tran.getCreditId())
                .flatMap(debt -> {
                    float currentMont = mont.getMont() - (debt + (tran.getMont() - oldTransaction.getMont()));

                    if(currentMont>0)
                        return createPayment(id, tran, log, transactionService);
                    else
                        return Mono.just(ResponseHandler.response("You don't have enough credits", HttpStatus.BAD_REQUEST, null));
                });
    }

    public static Mono<ResponseEntity<Object>> getOldPayment(String id, Transaction tran, Logger log, TransactionService transactionService, Mont mont)
    {
        return transactionService.find(id)
                .flatMap(transaction -> {
                    if(transaction!=null)
                        return CheckDebt(id,tran,log,transactionService,mont,transaction);
                    else
                        return Mono.just(ResponseHandler.response("Transaction not found", HttpStatus.BAD_REQUEST, null));
                });
    }

    public static Mono<ResponseEntity<Object>> CheckCreditMont(String id, Transaction tran, Logger log, TransactionService transactionService, ActiveService activeService)
    {
        return activeService.getMont(tran.getActiveId(),tran.getCreditId())
                .flatMap(responseMont -> {
                    if(responseMont.getData()!=null)
                    {
                        return getOldPayment(id,tran,log,transactionService,responseMont.getData());
                    }
                    else
                        return Mono.just(ResponseHandler.response("Mont Not Found", HttpStatus.BAD_REQUEST, null));
                });
    }

    public static Mono<ResponseEntity<Object>> UpdateTransactionSequence(String id, Transaction tran, Logger log, TransactionService transactionService, ActiveService activeService)
    {
        return CheckCreditMont(id, tran, log, transactionService,activeService);
    }
}
