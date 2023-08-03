package com.brainy.integration.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.brainy.integration.IntegrationTest;
import com.brainy.integration.model.ResponseString;

public class IndexControllerTest extends IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldStartServer() {
        ResponseEntity<ResponseString> response = 
                restTemplate.getForEntity("/", ResponseString.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseString body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals("server is up and running!", body.getData());
    }
}
