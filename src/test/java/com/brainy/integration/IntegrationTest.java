package com.brainy.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.brainy.model.entity.User;

/**
 * All integration tests mush inherit from this class as it does basic 
 * configuration for the tests. In addition it provide a default test and a rest
 * template user that can be used for testing.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    protected final User testUser;

    @Autowired
    protected TestRestTemplate restTemplate;

    public IntegrationTest() {
        testUser = IntegrationTestUtils.getRandomUser();
    }

    @BeforeEach
    public void setUpTestUser() {
        IntegrationTestUtils.registerUser(restTemplate, testUser);
    }
}
