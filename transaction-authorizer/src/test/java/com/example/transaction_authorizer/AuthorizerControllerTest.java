package com.example.transaction_authorizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

class AuthorizerControllerTest {
    private AuthorizerController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthorizerController();
    }

    @Test
    void testAuthorizeSuccessfulFoodTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId("123");
        transaction.setAccountId("ACC001");
        transaction.setAmount(500.0);
        transaction.setMerchant("GROCERY STORE");
        transaction.setMcc("5411");  // Food MCC

        ResponseEntity<String> response = controller.authorize(transaction);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("\"code\": \"00\""));
    }

    @Test
    void testAuthorizeSuccessfulMealTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId("124");
        transaction.setAccountId("ACC001");
        transaction.setAmount(200.0);
        transaction.setMerchant("UBER EATS");
        transaction.setMcc("5812");

        ResponseEntity<String> response = controller.authorize(transaction);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("\"code\": \"00\""));
    }

    @Test
    void testAuthorizeSuccessfulCashTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId("125");
        transaction.setAccountId("ACC001");
        transaction.setAmount(1000.0);
        transaction.setMerchant("PAG*JoseDaSilva");
        transaction.setMcc("0000");

        ResponseEntity<String> response = controller.authorize(transaction);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("\"code\": \"00\""));
    }

    @Test
    void testAuthorizeInsufficientFunds() {
        Transaction transaction = new Transaction();
        transaction.setId("126");
        transaction.setAccountId("ACC001");
        transaction.setAmount(5000.0);  // Amount greater than all balances
        transaction.setMerchant("GROCERY STORE");
        transaction.setMcc("5411");

        ResponseEntity<String> response = controller.authorize(transaction);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("\"code\": \"51\""));
    }

    @Test
    void testAuthorizeTimedOutTransaction() {
        Transaction transaction = new Transaction() {
            @Override
            public long getTimestamp() {
                return System.currentTimeMillis() - 200;
            }
        };
        transaction.setId("127");
        transaction.setAccountId("ACC001");
        transaction.setAmount(100.0);
        transaction.setMerchant("GROCERY STORE");
        transaction.setMcc("5411");

        // When
        ResponseEntity<String> response = controller.authorize(transaction);

        // Then
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Transaction failed after multiple retries"));
    }

    @Test
    void testAuthorizeFallbackToCash() {
        Transaction transaction = new Transaction();
        transaction.setId("128");
        transaction.setAccountId("ACC001");
        transaction.setAmount(1200.0);  // More than food balance but less than cash balance
        transaction.setMerchant("GROCERY STORE");
        transaction.setMcc("5411");

        ResponseEntity<String> response = controller.authorize(transaction);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("\"code\": \"00\""));
    }

    @Test
    void testUnknownMerchantWithValidMCC() {
        Transaction transaction = new Transaction();
        transaction.setId("129");
        transaction.setAccountId("ACC001");
        transaction.setAmount(200.0);
        transaction.setMerchant("NEW RESTAURANT");
        transaction.setMcc("5812");  // Restaurant MCC

        ResponseEntity<String> response = controller.authorize(transaction);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("\"code\": \"00\""));
    }

    @Test
    void testMultipleSuccessiveTransactions() {
        Transaction transaction1 = new Transaction();
        transaction1.setId("130");
        transaction1.setAccountId("ACC001");
        transaction1.setAmount(400.0);
        transaction1.setMerchant("GROCERY STORE");
        transaction1.setMcc("5411");

        Transaction transaction2 = new Transaction();
        transaction2.setId("131");
        transaction2.setAccountId("ACC001");
        transaction2.setAmount(300.0);
        transaction2.setMerchant("GROCERY STORE");
        transaction2.setMcc("5411");

        ResponseEntity<String> response1 = controller.authorize(transaction1);
        ResponseEntity<String> response2 = controller.authorize(transaction2);

        assertEquals(200, response1.getStatusCodeValue());
        assertEquals(200, response2.getStatusCodeValue());
        assertTrue(response1.getBody().contains("\"code\": \"00\""));
        assertTrue(response2.getBody().contains("\"code\": \"00\""));
    }

    @Test
    void testTransactionWithNullMCC() {
        Transaction transaction = new Transaction();
        transaction.setId("132");
        transaction.setAccountId("ACC001");
        transaction.setAmount(100.0);
        transaction.setMerchant("RANDOM MERCHANT");
        transaction.setMcc(null);

        ResponseEntity<String> response = controller.authorize(transaction);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("\"code\": \"00\""));
    }
}