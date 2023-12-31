package com.brainy.dao;

import org.springframework.lang.Nullable;
import com.brainy.model.entity.User;

public interface UserDao {
	User findUserByUsername(String username);

	@Nullable
	User findUserByEmail(String email);

	void registerUser(User user);

	void saveUserChanges(User user);
}
