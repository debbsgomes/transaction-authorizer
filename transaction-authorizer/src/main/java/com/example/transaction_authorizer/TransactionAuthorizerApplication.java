package com.example.transaction_authorizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransactionAuthorizerApplication {
	private static final Logger logger = LoggerFactory.getLogger(TransactionAuthorizerApplication.class);

	public static void main(String[] args) {
		try {
			SpringApplication.run(TransactionAuthorizerApplication.class, args);
			logger.info("Transaction Authorizer Application started successfully");
		} catch (Exception e) {
			logger.error("Error occurred while starting Transaction Authorizer Application", e);
		}
	}
}