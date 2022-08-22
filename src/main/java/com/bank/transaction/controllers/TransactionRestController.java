package com.bank.transaction.controllers;

import com.bank.transaction.controllers.helpers.TransactionRestControllerCreate;
import com.bank.transaction.controllers.helpers.TransactionRestControllerUpdate;
import com.bank.transaction.handler.ResponseHandler;
import com.bank.transaction.models.dao.TransactionDao;
import com.bank.transaction.models.documents.Transaction;
import com.bank.transaction.services.ActiveService;
import com.bank.transaction.services.ClientService;
import com.bank.transaction.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/transaction")
public class TransactionRestController
{
    @Autowired
    private TransactionDao dao;
    private static final Logger log = LoggerFactory.getLogger(TransactionRestController.class);

    @Autowired
    private ActiveService activeService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ClientService clientService;
    @GetMapping
    public Mono<ResponseEntity<Object>> findAll()
    {
        log.info("[INI] findAll Transaction");
        return transactionService.findAll()
                .flatMap(transactions -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, transactions)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] findAll Transaction"));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> find(@PathVariable String id)
    {
        log.info("[INI] find Transaction");
        return transactionService.find(id)
                .flatMap(transaction -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, transaction)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] find Transaction"));
    }

    @PostMapping()
    public Mono<ResponseEntity<Object>> create(@Valid @RequestBody Transaction tran)
    {
        log.info("[INI] create Transaction");

        return TransactionRestControllerCreate.CreateTransactionSequence(tran,log,transactionService,activeService)
                .doFinally(fin -> log.info("[END] create Transaction"));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> update(@PathVariable("id") String id, @RequestBody Transaction tran)
    {
        log.info("[INI] update Transaction");
        return TransactionRestControllerUpdate.UpdateTransactionSequence(id,tran,log,transactionService,activeService)
                .doFinally(fin -> log.info("[END] update Transaction"));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable("id") String id)
    {
        log.info("[INI] delete Transaction");
        log.info(id);
        return transactionService.delete(id)
                .flatMap(o -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, null)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Error", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] delete Transaction"));
    }

    @GetMapping("/clientTransactions/{idClient}")
    public Mono<ResponseEntity<Object>> findByIdClient(@PathVariable String idClient)
    {
        log.info("[INI] findByIdClient Transaction");
        return transactionService.findByIdClient(idClient)
                .flatMap(movements -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, movements)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] findByIdClient transaction"));
    }

    @GetMapping("/balance/{id}/{idCredit}")
    public Mono<ResponseEntity<Object>> getBalance(@PathVariable("id") String id, @PathVariable("idCredit") String idCredit)
    {
        log.info("[INI] getBalance transaction");
        log.info(id);

        return transactionService.getBalance(id,idCredit)
                .flatMap(balance -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, balance)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] getBalance transaction"));
    }

    @GetMapping("/debt/{id}/{idCredit}")
    public Mono<ResponseEntity<Object>> getDebt(@PathVariable("id") String id, @PathVariable("idCredit") String idCredit)
    {
        log.info("[INI] getDebt transaction");
        log.info(id);

        return transactionService.getTotalBalance(id,idCredit)
                .flatMap(balance -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, balance)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] getDebt transaction"));
    }

    @GetMapping("/balance/client/{idClient}")
    public Mono<ResponseEntity<Object>> getBalanceClient(@PathVariable("idClient") String idClient)
    {
        log.info("[INI] getBalanceClient");
        log.info(idClient);

        return transactionService.getTotalBalanceClient(idClient)
                .flatMap(balance -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, balance)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] getBalanceClient"));
    }

}
