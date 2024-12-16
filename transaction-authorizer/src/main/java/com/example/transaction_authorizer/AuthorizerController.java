package com.example.transaction_authorizer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

@RestController
public class AuthorizerController {
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

    @PostMapping("/authorize")
    public ResponseEntity<String> authorize(@RequestBody Transaction transaction) {
        String benefitCategory = merchantCategoryMap.getOrDefault(transaction.getMerchant(), null);

        if (benefitCategory == null) {
            if (transaction.getMcc().equals("5411") || transaction.getMcc().equals("5412")) {
                benefitCategory = "FOOD";
            } else if (transaction.getMcc().equals("5811") || transaction.getMcc().equals("5812")) {
                benefitCategory = "MEAL";
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
        return ResponseEntity.ok(responseBody);
    }
}
