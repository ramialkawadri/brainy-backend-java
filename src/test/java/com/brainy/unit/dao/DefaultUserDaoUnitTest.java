package com.brainy.unit.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.brainy.TestUtils;
import com.brainy.dao.DefaultUserDao;
import com.brainy.model.entity.User;

import jakarta.persistence.EntityManager;

public class DefaultUserDaoUnitTest {

	private EntityManager entityManager;
	private DefaultUserDao userDAO;

	public DefaultUserDaoUnitTest() {
		entityManager = Mockito.mock();
		userDAO = new DefaultUserDao(entityManager);
	}

	@Test
	public void shouldFindUserByUsername() {
		// Arrange
		User testUser = TestUtils.generateRandomUser();

		Mockito.when(entityManager.find(User.class, "user")).thenReturn(testUser);

		// Act
		User returnValue = userDAO.findUserByUserName("user");

		// Assert
		Assertions.assertEquals(testUser, returnValue);
	}

	@Test
	public void shouldRegisterUser() {
		// Arrange
		User testUser = TestUtils.generateRandomUser();

		// Act
		userDAO.registerUser(testUser);

		// Assert
		Mockito.verify(entityManager).persist(testUser);
	}

	@Test
	public void shouldSaveUserChanges() {
		// Arrange
		User user = TestUtils.generateRandomUser();

		// Act
		userDAO.saveUserChanges(user);

		// Assert
		Mockito.verify(entityManager).merge(user);
	}

}
