package com.brainy.dao;

import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import com.brainy.model.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class DefaultUserDao implements UserDao {

	private EntityManager entityManager;

	public DefaultUserDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public User findUserByUsername(String username) {
		User user = entityManager.find(User.class, username);
		return user;
	}

	@Override
	@Nullable
	public User findUserByEmail(String email) {
		TypedQuery<User> query =
				entityManager.createQuery("FROM User u WHERE u.email=:email", User.class);
		query.setParameter("email", email);

		List<User> results = query.getResultList();

		if (results.isEmpty())
			return null;

		return results.get(0);
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
