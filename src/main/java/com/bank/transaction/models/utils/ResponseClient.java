package com.bank.transaction.models.utils;

import com.bank.transaction.models.documents.Client;
import lombok.Data;

@Data
public class ResponseClient
{
    private Client data;

    private String message;

    private String status;

}
