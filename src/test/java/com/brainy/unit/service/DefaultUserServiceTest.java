package com.brainy.unit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.brainy.entity.User;
import com.brainy.service.DefaultUserService;

import jakarta.persistence.EntityManager;

public class DefaultUserServiceTest {
    
    private EntityManager entityManager;
    private DefaultUserService userService;

    public DefaultUserServiceTest() {
        entityManager = Mockito.mock();
        userService = new DefaultUserService(entityManager);
    }

    @Test
    public void shouldGetUserByUsername() {
        User mockUser = new User();

        Mockito.when(entityManager.find(User.class, "user")).thenReturn(mockUser);

        User user = userService.getUserByUsername("user");

        Mockito.verify(entityManager).find(User.class, "user");

        Assertions.assertEquals(mockUser, user);
    }
}
