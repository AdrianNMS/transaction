package com.bank.transaction.models.dao;

import com.bank.transaction.models.documents.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransactionDao extends ReactiveMongoRepository<Transaction, String>
{
}
