package com.b9.json.jsonplatform.transaction.application.handler;

import com.b9.json.jsonplatform.transaction.domain.Transaction;
import com.b9.json.jsonplatform.transaction.domain.TransactionType;

public interface TransactionHandler {
    TransactionType supportedType();
    void execute(Transaction transaction);
}
