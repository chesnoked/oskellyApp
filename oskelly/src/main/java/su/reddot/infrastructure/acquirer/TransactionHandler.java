package su.reddot.infrastructure.acquirer;

import su.reddot.infrastructure.acquirer.impl.mdm.type.TransactionInfo;

@FunctionalInterface
public interface TransactionHandler {
    void handleTransaction(TransactionInfo t);
}
