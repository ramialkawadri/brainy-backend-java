package com.brainy.unit.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.brainy.controller.IndexController;
import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;

public class IndexControllerUnitTest {
    
    private IndexController indexController;

    public IndexControllerUnitTest() {
        indexController = new IndexController();
    }

    @Test
    public void shouldGetStatus() {
        Response<String> response = indexController.getServerStatus();

        Assertions.assertEquals(ResponseStatus.SUCCESS, response.getStatus());
        Assertions.assertEquals("server is up and running!", response.getData());
    }    
}
