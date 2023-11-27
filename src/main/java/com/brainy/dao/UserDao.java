package com.brainy.dao;

import com.brainy.model.entity.User;

public interface UserDao {

	User findUserByUserName(String username);

	void registerUser(User user);

	void saveUserChanges(User user);
}
