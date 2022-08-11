package com.bank.transaction.services;

import com.bank.transaction.models.documents.Parameter;
import com.bank.transaction.models.utils.ResponseParameter;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ParameterService {

    Mono<ResponseParameter> findByCode(Integer code);

    List<Parameter> getParameter(List<Parameter> listParameter, Integer code);
}
