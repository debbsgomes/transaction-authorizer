package com.example.transaction_authorizer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizerController {
    @PostMapping("/authorize")
    public ResponseEntity<String> authorize(@RequestBody Transaction transaction) {
        String benefitCategory;
        if (transaction.getMcc().equals("5411") || transaction.getMcc().equals("5412")) {
            benefitCategory = "FOOD";
        } else if (transaction.getMcc().equals("5811") || transaction.getMcc().equals("5812")) {
            benefitCategory = "MEAL";
        } else {
            benefitCategory = "CASH";
        }

        String responseCode = "00";
        String responseBody = "{\"code\": \"" + responseCode + "\"}";
        return ResponseEntity.ok(responseBody);
    }
}
