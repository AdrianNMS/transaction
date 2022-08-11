package com.bank.transaction.controllers;

import com.bank.transaction.handler.ResponseHandler;
import com.bank.transaction.models.dao.TransactionDao;
import com.bank.transaction.models.documents.Parameter;
import com.bank.transaction.models.documents.Transaction;
import com.bank.transaction.models.utils.UtilParameter;
import com.bank.transaction.services.ActiveService;
import com.bank.transaction.services.ClientService;
import com.bank.transaction.services.ParameterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

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
    private ParameterService parameterService;

    @Autowired
    private ClientService clientService;
    @GetMapping
    public Mono<ResponseEntity<Object>> findAll()
    {
        log.info("[INI] findAll Transaction");
        return dao.findAll()
                .doOnNext(transaction -> log.info(transaction.toString()))
                .collectList()
                .map(transactions -> ResponseHandler.response("Done", HttpStatus.OK, transactions))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] findAll Transaction"));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> find(@PathVariable String id)
    {
        log.info("[INI] find Transaction");
        return dao.findById(id)
                .doOnNext(transaction -> log.info(transaction.toString()))
                .map(transaction -> ResponseHandler.response("Done", HttpStatus.OK, transaction))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] find Transaction"));
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> create(@RequestBody Transaction tran)
    {
        log.info("[INI] create Transaction");
        activeService.findByCode(tran.getActiveId()).
                flatMap(responseActive -> {
                    if(responseActive.getData()==null){
                        return Mono.just(ResponseHandler.response("Does not have active", HttpStatus.BAD_REQUEST, null));
                    }

                    String typeClient = clientService.findByCode(tran.getClientId())
                            .map(responseClient -> responseClient.getData()!=null?responseClient.getData().getType():"").block();

                    if(typeClient!=null && typeClient.length()>0){
                        return Mono.just(ResponseHandler.response("OK", HttpStatus.OK, "success"));

                    }else{
                        return Mono.just(ResponseHandler.response("Does not have client", HttpStatus.BAD_REQUEST, null));
                    }
                })
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)));

        return dao.save(tran)
                .doOnNext(transaction -> log.info(transaction.toString()))
                .map(transaction -> ResponseHandler.response("Done", HttpStatus.OK, transaction)                )
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] create Transaction"));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> update(@PathVariable("id") String id, @RequestBody Transaction act)
    {
        log.info("[INI] update Transaction");
        return dao.existsById(id).flatMap(check -> {
            if (check)
                return dao.save(act)
                        .doOnNext(transaction -> log.info(transaction.toString()))
                        .map(transaction -> ResponseHandler.response("Done", HttpStatus.OK, transaction)                )
                        .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));

            else
                return Mono.just(ResponseHandler.response("Not found", HttpStatus.NOT_FOUND, null));

        }).doFinally(fin -> log.info("[END] update Transaction"));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable("id") String id)
    {
        log.info("[INI] delete Transaction");
        return dao.existsById(id).flatMap(check -> {
            if (check)
                return dao.deleteById(id).then(Mono.just(ResponseHandler.response("Done", HttpStatus.OK, null)));
            else
                return Mono.just(ResponseHandler.response("Not found", HttpStatus.NOT_FOUND, null));
        }).doFinally(fin -> log.info("[END] delete Transaction"));
    }

    @GetMapping("/test-parameter")
    public Mono<ResponseEntity<Object>> getParamter() {
        return activeService.findByCode("62f430a67ff93b6655f213b6")
                .doOnNext(parameter -> log.info(parameter.toString()))
                .flatMap(data -> {
                            if(data.getData()!=null){

                                return dao.findAll()
                                        .doOnNext(person -> log.info(person.toString()))
                                        .collectList().map(pasives -> ResponseHandler.response("Done", HttpStatus.OK, pasives))
                                        .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));

                            }else {
                                return Mono.just( ResponseHandler.response("Error in Parameter", HttpStatus.BAD_REQUEST, null));
                            }
                        }

                ).doFinally(fin -> log.info("[END] Parameter Pasive"));

    }
}
