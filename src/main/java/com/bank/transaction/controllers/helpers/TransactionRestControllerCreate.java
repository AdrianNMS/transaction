package com.bank.transaction.controllers.helpers;

import com.bank.transaction.handler.ResponseHandler;
import com.bank.transaction.models.documents.Transaction;
import com.bank.transaction.models.enums.TypeTransaction;
import com.bank.transaction.models.utils.Mont;
import com.bank.transaction.services.ActiveService;
import com.bank.transaction.services.ClientService;
import com.bank.transaction.services.TransactionService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class TransactionRestControllerCreate
{
    public static Mono<ResponseEntity<Object>> createPayment(Transaction tran, Logger log, TransactionService transactionService)
    {
        tran.setDateRegister(LocalDateTime.now());

        return transactionService.create(tran)
                .doOnNext(transaction -> log.info(transaction.toString()))
                .flatMap(transaction -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, transaction)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
    }

    public static Mono<ResponseEntity<Object>> CheckDebt(Transaction tran, Logger log, TransactionService transactionService, Mont mont)
    {
        return transactionService.getBalance(tran.getActiveId(), tran.getCreditId())
                .flatMap(debt -> {
                    log.info(debt.toString());
                    float currentMont = mont.getMont() - (debt + tran.getMont());

                    if(currentMont>=0)
                        return createPayment(tran, log, transactionService);
                    else
                        return Mono.just(ResponseHandler.response("You don't have enough credits", HttpStatus.BAD_REQUEST, null));
                });
    }

    public static Mono<ResponseEntity<Object>> CheckCreditMont(Transaction tran, Logger log, TransactionService transactionService, ActiveService activeService)
    {
        return activeService.getMont(tran.getActiveId(),tran.getCreditId())
                .flatMap(responseMont -> {
                    if(responseMont.getData()!=null)
                    {
                        log.info(responseMont.toString());
                        return CheckDebt(tran,log,transactionService,responseMont.getData());
                    }
                    else
                        return Mono.just(ResponseHandler.response("Mont Not Found", HttpStatus.BAD_REQUEST, null));
                });
    }

    public static Mono<ResponseEntity<Object>> CheckActiveType(Transaction tran, Logger log, TransactionService transactionService, ActiveService activeService)
    {
        return activeService.findType(tran.getActiveId())
                .flatMap(responseActive ->
                {
                    log.info(responseActive.toString());
                    if(responseActive.getData()!=null)
                    {
                        tran.setTypeTransaction(TypeTransaction.fromInteger(responseActive.getData()));
                        return CheckCreditMont(tran, log, transactionService,activeService);
                    }
                    else
                        return Mono.just(ResponseHandler.response("Active Not Found", HttpStatus.BAD_REQUEST, null));
                });
    }

    public static Mono<ResponseEntity<Object>> CreateTransactionSequence(Transaction tran, Logger log, TransactionService transactionService, ActiveService activeService)
    {
        return CheckActiveType(tran, log, transactionService,activeService);
    }

}
