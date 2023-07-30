package com.brainy.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.brainy.model.entity.User;

/**
 * All integration tests mush inherit from this class as it does basic 
 * configuration for the tests. In addition it provide a default test and a rest
 * template user that can be used for testing.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class IntegrationTest {

    protected final User testUser = new User(
        "test", "testPassword", "test@test.com", "firstName", "lastName");

    @Autowired
    protected TestRestTemplate restTemplate;

    @BeforeEach
    public void setUpTestUser() {
        IntegrationTestUtils.registerUser(restTemplate, testUser);
    }
}
