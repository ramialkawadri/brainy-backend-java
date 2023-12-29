package com.brainy.dao;

import org.springframework.stereotype.Repository;

import com.brainy.model.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
public class DefaultUserDao implements UserDao {

	private EntityManager entityManager;

	public DefaultUserDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public User findUserByUsername(String username) {
		// All usernames are in lower case
		User user = entityManager.find(User.class, username.toLowerCase());
		return user;
	}

	@Override
	@Transactional
	public void registerUser(User user) {
		entityManager.persist(user);
	}

	@Override
	@Transactional
	public void saveUserChanges(User user) {
		entityManager.merge(user);
	}
}
