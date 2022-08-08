package com.bank.transaction.controllers;

import com.bank.transaction.handler.ResponseHandler;
import com.bank.transaction.models.dao.TransactionDao;
import com.bank.transaction.models.documents.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/transaction")
public class TransactionRestController
{
    @Autowired
    private TransactionDao dao;
    private static final Logger log = LoggerFactory.getLogger(TransactionRestController.class);

    @GetMapping
    public Mono<ResponseEntity<Object>> findAll()
    {
        return dao.findAll()
                .doOnNext(transaction -> log.info(transaction.toString()))
                .collectList()
                .map(transactions -> ResponseHandler.response("Done", HttpStatus.OK, transactions))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> find(@PathVariable String id)
    {
        return dao.findById(id)
                .doOnNext(transaction -> log.info(transaction.toString()))
                .map(transaction -> ResponseHandler.response("Done", HttpStatus.OK, transaction))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> create(@RequestBody Transaction tran)
    {

        return dao.save(tran)
                .doOnNext(transaction -> log.info(transaction.toString()))
                .map(transaction -> ResponseHandler.response("Done", HttpStatus.OK, transaction)                )
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> update(@PathVariable("id") String id, @RequestBody Transaction act)
    {
        return dao.existsById(id).flatMap(check -> {
            if (check)
                return dao.save(act)
                        .doOnNext(transaction -> log.info(transaction.toString()))
                        .map(transaction -> ResponseHandler.response("Done", HttpStatus.OK, transaction)                )
                        .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
            else
                return Mono.just(ResponseHandler.response("Not found", HttpStatus.NOT_FOUND, null));

        });
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable("id") String id)
    {
        log.info(id);

        return dao.existsById(id).flatMap(check -> {
            if (check)
                return dao.deleteById(id).then(Mono.just(ResponseHandler.response("Done", HttpStatus.OK, null)));
            else
                return Mono.just(ResponseHandler.response("Not found", HttpStatus.NOT_FOUND, null));
        });
    }
}