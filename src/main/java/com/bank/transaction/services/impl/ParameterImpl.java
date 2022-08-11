package com.bank.transaction.services.impl;

import com.bank.transaction.models.documents.Parameter;
import com.bank.transaction.models.utils.ResponseParameter;
import com.bank.transaction.services.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParameterImpl implements ParameterService {

    @Qualifier("getWebClientParameter")
    @Autowired
    WebClient webClient;

    @Override
    public Mono<ResponseParameter> findByCode(Integer code)
    {
        return webClient.get()
                .uri("/api/parameter/catalogue/"+ code)
                .retrieve()
                .bodyToMono(ResponseParameter.class);
    }

    public List<Parameter> getParameter(List<Parameter> listParameter, Integer code) {
        return listParameter.stream().filter(x -> x.getCode().toString().equals(code.toString()) )
                .collect(Collectors.toList());
    }
}
