package com.bank.transaction.models.utils;

import com.bank.transaction.models.documents.Parameter;

import java.util.List;
import java.util.stream.Collectors;

public class UtilParameter {

    public static List<Parameter> getParameter(List<Parameter> listParameter, Integer code) {

        return listParameter.stream().filter(x -> x.getCode().toString().equals(code.toString()) )
                .collect(Collectors.toList());
    }
}
