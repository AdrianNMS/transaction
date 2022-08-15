package com.bank.transaction.controllers;

import com.bank.transaction.models.utils.Mont;
import com.bank.transaction.handler.ResponseHandler;
import com.bank.transaction.models.dao.TransactionDao;
import com.bank.transaction.models.documents.Transaction;
import com.bank.transaction.services.ActiveService;
import com.bank.transaction.services.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

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
                .switchIfEmpty(Mono.just(ResponseHandler.response("No Content", HttpStatus.BAD_REQUEST, null)))
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
                .switchIfEmpty(Mono.just(ResponseHandler.response("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] find Transaction"));
    }

    @PostMapping("{type}")
    public Mono<ResponseEntity<Object>> create(@PathVariable("type") String type,@Valid @RequestBody Transaction tran)
    {
        log.info("[INI] create Transaction");

        String typeName = "";
        if(type.equals("3")){
            typeName = "PERSONAL";
        }else if(type.equals("4")){
            typeName = "COMPANY";
        }

        String finalTypeName = typeName;

        return activeService.findByCode(tran.getActiveId())
                .doOnNext(transaction -> log.info(transaction.toString())).
                flatMap(responseActive -> {
                    log.info(tran.toString());

                    if(responseActive.getData()==null){
                        return Mono.just(ResponseHandler.response("Does not have active", HttpStatus.BAD_REQUEST, null));
                    }
                    else
                        return activeService.getMont(tran.getActiveId(),tran.getCreditId()).flatMap(
                                responseMont -> {
                                    if(responseMont.getData()!=null)
                                    {
                                        float montDiference = responseMont.getData().getMont() - tran.getMont();

                                        Mont mont = new Mont();
                                        mont.setMont(montDiference);

                                        if(montDiference>0)
                                        {
                                            return clientService.findByCode(tran.getClientId())
                                                    .doOnNext(transaction -> log.info(transaction.toString()))
                                                    .flatMap(responseClient -> {
                                                        if(responseClient.getData() == null){
                                                            return Mono.just(ResponseHandler.response("Does not have client", HttpStatus.BAD_REQUEST, null));
                                                        }

                                                        if(!finalTypeName.equals(responseClient.getData().getType())){
                                                            return Mono.just(ResponseHandler.response("The Active is not enabled for the client", HttpStatus.BAD_REQUEST, null));
                                                        }
                                                        tran.setDateRegister(LocalDateTime.now());

                                                        return activeService.setMont(tran.getActiveId(),tran.getCreditId(),mont)
                                                                .flatMap(responseMont1 -> {
                                                                    if(responseMont1.getStatus().equals("OK"))
                                                                        return dao.save(tran)
                                                                                .doOnNext(transaction -> log.info(transaction.toString()))
                                                                                .map(transaction -> ResponseHandler.response("Done", HttpStatus.OK, transaction)                )
                                                                                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
                                                                    else
                                                                        return Mono.just(ResponseHandler.response("Not Found", HttpStatus.BAD_REQUEST, null));
                                                                });

                                                    });

                                        }
                                        else
                                            return Mono.just(ResponseHandler.response("You don't have enough credit", HttpStatus.BAD_REQUEST, null));

                                    }
                                    else
                                        return Mono.just(ResponseHandler.response("Not Found", HttpStatus.BAD_REQUEST, null));

                                }
                        );
                })
                .switchIfEmpty(Mono.just(ResponseHandler.response("Active No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] create Transaction"));

    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> update(@PathVariable("id") String id, @RequestBody Transaction act)
    {
        log.info("[INI] update Transaction");
        return dao.existsById(id).flatMap(check -> {
            if (check){
                act.setDateUpdate(LocalDateTime.now());
                return dao.save(act)
                        .doOnNext(transaction -> log.info(transaction.toString()))
                        .map(transaction -> ResponseHandler.response("Done", HttpStatus.OK, transaction)                )
                        .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
            }
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

    @GetMapping("/clientTransactions/{idClient}")
    public Mono<ResponseEntity<Object>> findByIdClient(@PathVariable String idClient)
    {
        log.info("[INI] findByIdClient Transaction");
        return dao.findAll()
                .filter(transaction ->
                        transaction.getClientId().equals(idClient)
                )
                .collectList()
                .doOnNext(transaction -> log.info(transaction.toString()))
                .map(movements -> ResponseHandler.response("Done", HttpStatus.OK, movements))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] findByIdClient transaction"));
    }

    @GetMapping("/balance/{id}/{idCredit}")
    public Mono<ResponseEntity<Object>> getBalance(@PathVariable("id") String id, @PathVariable("idCredit") String idCredit)
    {
        LocalDateTime dateNow = LocalDateTime.now();
        log.info("[INI] getBalance transaction");
        log.info(id);
        AtomicReference<Float> balance = new AtomicReference<>((float) 0);
        return dao.findAll()
                .filter(transaction ->
                        transaction.getDateRegister().getMonthValue() == dateNow.getMonthValue()
                                && transaction.getDateRegister().getYear() == dateNow.getYear() &&
                                (transaction.getActiveId().equals(id) && transaction.getCreditId().equals(idCredit))
                )
                .collectList()
                .map(transactions -> ResponseHandler.response("Done", HttpStatus.OK, balance.get()))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] getBalance transaction"));
    }

}
