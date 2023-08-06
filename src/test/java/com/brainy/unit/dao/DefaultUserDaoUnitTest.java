package com.brainy.unit.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.brainy.TestUtils;
import com.brainy.dao.DefaultUserDAO;
import com.brainy.model.entity.User;

import jakarta.persistence.EntityManager;

public class DefaultUserDaoUnitTest {

    private EntityManager entityManager;
    private DefaultUserDAO userDAO;

    public DefaultUserDaoUnitTest() {
        entityManager = Mockito.mock();
        userDAO = new DefaultUserDAO(entityManager);
    }

    @Test
    public void shouldFindUserByUsername() {
        User testUser = TestUtils.generateRandomUser();

        Mockito.when(entityManager.find(User.class, "user")).thenReturn(testUser);

        User returnValue = userDAO.findUserByUserName("user");

        Assertions.assertEquals(testUser, returnValue);
    }

    @Test
    public void shouldRegisterUser() {
        User testUser = TestUtils.generateRandomUser();

        userDAO.registerUser(testUser);

        Mockito.verify(entityManager).persist(testUser);
    }

    @Test
    public void shouldSaveUserChanges() {
        User user = TestUtils.generateRandomUser();
        userDAO.saveUserChanges(user);
        Mockito.verify(entityManager).merge(user);
    }
    
}
