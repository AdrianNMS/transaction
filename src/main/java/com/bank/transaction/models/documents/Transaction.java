package com.bank.transaction.models.documents;

import com.bank.transaction.models.enums.TypeTransaction;
import com.bank.transaction.models.utils.Audit;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Data
@Document(collection = "transactions")
public class Transaction extends Audit
{
    @Id
    private String id;
    @NotNull(message = "activeId must not be null")
    private String activeId;
    @NotNull(message = "clientId must not be null")
    private String clientId;
    @NotNull(message = "creditId must not be null")
    private String creditId;
    @NotNull(message = "mont must not be null")
    private float mont;

    private TypeTransaction typeTransaction;

    private String seller;
    private String ruc;

}
