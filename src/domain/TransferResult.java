package domain;

public class TransferResult {
    private final boolean success;
    private final String transactionId;

    public TransferResult(boolean success, String transactionId) {
        this.success = success;
        this.transactionId = transactionId;
    }

    public boolean isSuccess()        { return success; }
    public String  getTransactionId() { return transactionId; }
}
