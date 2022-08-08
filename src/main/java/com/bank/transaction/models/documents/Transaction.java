package com.bank.transaction.models.documents;

import com.bank.transaction.models.utils.Audit;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "transactions")
public class Transaction extends Audit
{
    @Id
    private String id;
    private String activeId;
    private String clientId;
    private String creditId;
    private float mont;
    private String seller;
    private String ruc;

}
