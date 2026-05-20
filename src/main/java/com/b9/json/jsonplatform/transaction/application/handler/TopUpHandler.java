package com.b9.json.jsonplatform.transaction.application.handler;

import com.b9.json.jsonplatform.wallet.application.WalletService;
import com.b9.json.jsonplatform.transaction.domain.Transaction;
import com.b9.json.jsonplatform.transaction.domain.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class TopUpHandler implements TransactionHandler {

    private final WalletService walletService;

    public TopUpHandler(WalletService walletService) {
        this.walletService = walletService;
    }

    @Override
    public TransactionType supportedType() {
        return TransactionType.TOP_UP;
    }

    @Override
    public void execute(Transaction transaction) {
        walletService.increaseBalance(transaction.getWalletId(), transaction.getAmount());
    }
}
