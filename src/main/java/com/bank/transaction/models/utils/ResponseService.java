package com.bank.transaction.models.utils;

import com.bank.transaction.models.documents.Parameter;
import lombok.Data;

import java.util.List;

@Data
public class ResponseService
{
    private List<Parameter> data;

    private String message;

    private String status;

}
