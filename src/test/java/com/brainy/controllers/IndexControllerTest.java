package com.brainy.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.brainy.wrappers.ResponseString;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IndexControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldStartServer() {
        ResponseEntity<ResponseString> response = 
                restTemplate.getForEntity("/", ResponseString.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseString body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals("Server is up and running!", body.getData());
    }
}
