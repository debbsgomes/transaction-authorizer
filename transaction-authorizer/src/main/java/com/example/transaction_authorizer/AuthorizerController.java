package com.example.transaction_authorizer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

@RestController
@Tag(name = "Transaction Authorizer", description = "API for authorizing transactions based on merchant category and available balance")
public class AuthorizerController {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizerController.class);
    private static final long TIMEOUT_THRESHOLD = 100;
    private static final int MAX_RETRY_COUNT = 3;

    private double foodBalance = 1000.0;
    private double mealBalance = 500.0;
    private double cashBalance = 2000.0;
    private Map<String, String> merchantCategoryMap = new HashMap<>();

    public AuthorizerController() {
        merchantCategoryMap.put("UBER EATS", "MEAL");
        merchantCategoryMap.put("UBER TRIP", "CASH");
        merchantCategoryMap.put("PAG*JoseDaSilva", "CASH");
        merchantCategoryMap.put("PICPAY*BILHETEUNICO", "CASH");
    }

    @Operation(
            summary = "Authorize a transaction",
            description = "Authorizes a transaction based on the merchant category and available balance. " +
                    "Supports FOOD, MEAL, and CASH categories with automatic fallback to CASH.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Transaction processed successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"code\": \"00\"}"))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error or transaction timeout",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "Transaction failed after multiple retries")))
            }
    )

    @PostMapping("/authorize")
    public ResponseEntity<String> authorize(@RequestBody Transaction transaction) {
        try {
            logger.info("Received transaction request: {}", transaction);

            if (isTransactionTimedOut(transaction)) {
                logger.warn("Transaction {} timed out", transaction.getId());
                return retryTransaction(transaction);
            }

            String benefitCategory = merchantCategoryMap.getOrDefault(transaction.getMerchant(), null);
            if (benefitCategory == null) {
                String mcc = transaction.getMcc();
                if (mcc != null) {
                    if (Objects.equals(mcc, "5411") || Objects.equals(mcc, "5412")) {
                        benefitCategory = "FOOD";
                    } else if (Objects.equals(mcc, "5811") || Objects.equals(mcc, "5812")) {
                        benefitCategory = "MEAL";
                    } else {
                        benefitCategory = "CASH";
                    }
                } else {
                    benefitCategory = "CASH";
                }
            }

            double balance;
            if (benefitCategory.equals("FOOD")) {
                balance = foodBalance;
            } else if (benefitCategory.equals("MEAL")) {
                balance = mealBalance;
            } else {
                balance = cashBalance;
            }

            String responseCode;
            if (balance >= transaction.getAmount()) {
                responseCode = "00";
                if (benefitCategory.equals("FOOD")) {
                    foodBalance -= transaction.getAmount();
                } else if (benefitCategory.equals("MEAL")) {
                    mealBalance -= transaction.getAmount();
                } else {
                    cashBalance -= transaction.getAmount();
                }
            } else if (cashBalance >= transaction.getAmount()) {
                responseCode = "00";
                cashBalance -= transaction.getAmount();
            } else {
                responseCode = "51";
            }

            String responseBody = "{\"code\": \"" + responseCode + "\"}";
            logger.info("Transaction processed successfully");
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            logger.error("Error occurred while processing transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    private boolean isTransactionTimedOut(Transaction transaction) {
        long currentTime = System.currentTimeMillis();
        long transactionTime = transaction.getTimestamp();
        return (currentTime - transactionTime) > TIMEOUT_THRESHOLD;
    }

    private ResponseEntity<String> retryTransaction(Transaction transaction) {
        int retryCount = transaction.getRetryCount();
        if (retryCount < MAX_RETRY_COUNT) {
            long backoffTime = calculateBackoffTime(retryCount);
            logger.info("Retrying transaction {} after {} ms", transaction.getId(), backoffTime);
            try {
                Thread.sleep(backoffTime);
            } catch (InterruptedException e) {
                logger.error("Retry interrupted for transaction {}", transaction.getId(), e);
                Thread.currentThread().interrupt();
            }
            transaction.incrementRetryCount();
            return authorize(transaction);
        } else {
            logger.error("Max retry count reached for transaction {}", transaction.getId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Transaction failed after multiple retries");
        }
    }

    private long calculateBackoffTime(int retryCount) {
        return (long) Math.pow(2, retryCount) * 100;
    }
}
