package com.brainy.unit.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.brainy.dao.DefaultUserDAO;
import com.brainy.model.entity.User;

import jakarta.persistence.EntityManager;

public class DefaultUserDaoTest {

    private EntityManager entityManager;
    private DefaultUserDAO userDAO;

    public DefaultUserDaoTest() {
        entityManager = Mockito.mock();
        userDAO = new DefaultUserDAO(entityManager);
    }

    @Test
    public void shouldFindUserByUsername() {
        User testUser = new User();

        Mockito.when(entityManager.find(User.class, "user")).thenReturn(testUser);

        User returnValue = userDAO.findUserByUserName("user");

        Assertions.assertEquals(testUser, returnValue);
    }

    @Test
    public void shouldRegisterUser() {
        User testUser = new User();

        userDAO.registerUser(testUser);

        Mockito.verify(entityManager).persist(testUser);
    }
    
}
