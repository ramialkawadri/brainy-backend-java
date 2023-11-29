package com.brainy.dao;

import com.brainy.model.entity.User;

public interface UserDao {

	User findUserByUsername(String username);

	void registerUser(User user);

	void saveUserChanges(User user);
}
