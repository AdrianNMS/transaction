package com.bank.transaction.models.utils;

import com.bank.transaction.models.documents.Active;
import lombok.Data;

import java.util.List;

@Data
public class ResponseActive
{
    private Active data;

    private String message;

    private String status;

}
