package com.example.transaction_authorizer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.main.banner-mode=off"
})
class TransactionAuthorizerApplicationTest {

	@Test
	void contextLoads(ApplicationContext context) {
		assertNotNull(context);
	}

	@Test
	void mainMethodStartsSuccessfully() {
		assertDoesNotThrow(() ->
				TransactionAuthorizerApplication.main(new String[]{})
		);
	}

	@Test
	void mainMethodHandlesException() {
		String[] invalidArgs = {"--invalid.property=value"};
		assertDoesNotThrow(() ->
				TransactionAuthorizerApplication.main(invalidArgs)
		);
	}
}
