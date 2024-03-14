package com.brainy.unit.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.brainy.BrainyApplication;
import com.brainy.TestUtils;
import com.brainy.dao.DefaultUserDao;
import com.brainy.model.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest(classes = BrainyApplication.class)
public class DefaultUserDaoUnitTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private DefaultUserDao userDAO;

	@Test
	public void shouldFindUserByUsername() {
		// Arrange

		User testUser = TestUtils.generateRandomUser();
		entityManager.persist(testUser);

		// Act

		User actual = userDAO.findUserByUsername(testUser.getUsername());

		// Assert

		Assertions.assertEquals(testUser, actual);
	}

	@Test
	public void shouldFindUserByEmail() {
		// Arrange

		User testUser = TestUtils.generateRandomUser();
		entityManager.persist(testUser);

		// Act

		User actual = userDAO.findUserByEmail(testUser.getEmail());

		// Assert

		Assertions.assertEquals(testUser, actual);
	}

	@Test
	public void shouldReturnNullWhenEmailIsNotFound() {
		// Arrange

		User testUser = TestUtils.generateRandomUser();
		entityManager.persist(testUser);

		// Act

		User actual = userDAO.findUserByEmail(testUser.getEmail() + "ss");

		// Assert

		Assertions.assertNull(actual);
	}

	@Test
	public void shouldRegisterUser() {
		// Arrange

		User testUser = TestUtils.generateRandomUser();

		// Act

		userDAO.registerUser(testUser);
		User actual = entityManager.find(User.class, testUser.getUsername());

		// Assert

		Assertions.assertEquals(testUser, actual);
	}

	@Test
	public void shouldSaveUserChanges() {
		// Arrange

		User testUser = TestUtils.generateRandomUser();
		entityManager.persist(testUser);
		testUser.setEmail("New email");

		// Act

		userDAO.saveUserChanges(testUser);
		User actual = entityManager.find(User.class, testUser.getUsername());

		// Assert

		Assertions.assertEquals(testUser.getEmail(), actual.getEmail());
	}
}
