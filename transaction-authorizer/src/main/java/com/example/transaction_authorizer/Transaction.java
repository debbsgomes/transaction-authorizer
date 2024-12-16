package com.example.transaction_authorizer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transaction details for authorization")
public class Transaction {
    @Schema(description = "Unique identifier for the transaction", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @Schema(description = "Account identifier", example = "ACC001")
    private String accountId;

    @Schema(description = "Transaction amount", example = "100.00")
    private double amount;

    @Schema(description = "Merchant name", example = "UBER EATS")
    private String merchant;

    @Schema(description = "Merchant Category Code", example = "5811")
    private String mcc;

    @Schema(description = "Transaction timestamp (automatically set)", hidden = true)
    private long timestamp;

    @Schema(description = "Retry count for failed transactions", hidden = true)
    private int retryCount;

    public Transaction() {
        this.timestamp = System.currentTimeMillis();
        this.retryCount = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void incrementRetryCount() {
        retryCount++;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", amount=" + amount +
                ", merchant='" + merchant + '\'' +
                ", mcc='" + mcc + '\'' +
                ", timestamp=" + timestamp +
                ", retryCount=" + retryCount +
                '}';
    }
}
