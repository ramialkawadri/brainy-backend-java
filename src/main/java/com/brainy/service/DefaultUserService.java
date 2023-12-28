package com.brainy.service;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.brainy.dao.UserDao;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UserRegistrationRequest;

import jakarta.persistence.EntityExistsException;

@Service
public class DefaultUserService implements UserService {

	private UserDao userDao;
	private PasswordEncoder passwordEncoder;

	public DefaultUserService(UserDao userDao, PasswordEncoder passwordEncoder) {
		this.userDao = userDao;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User findUserByUsername(String username) {
		return userDao.findUserByUsername(username);
	}

	@Override
	public void registerUserFromRequest(UserRegistrationRequest request)
			throws BadRequestException {

		User user = request.toUser();

		encodeAndUpdateUserPassword(user, user.getPassword());

		try {
			userDao.registerUser(user);
		} catch (EntityExistsException | DataIntegrityViolationException e) {
			throw new BadRequestException("a user with the same username or email already exists");
		}
	}

	@Override
	public boolean isTokenStillValidForUser(Instant issuedAt, String username) {
		User user = findUserByUsername(username);

		if (user == null)
			return false;

		Instant passwordChangeDate = user.getPasswordChangeDate().toInstant();
		Instant logoutDate = user.getLogoutDate().toInstant();

		return passwordChangeDate.isBefore(issuedAt) && logoutDate.isBefore(issuedAt);
	}

	@Override
	public void logoutUser(User user) {
		Timestamp now = Timestamp.from(Instant.now());
		user.setLogoutDate(now);
		userDao.saveUserChanges(user);
	}

	@Override
	public void updateUserPassword(User user, String newPassword) {
		encodeAndUpdateUserPassword(user, newPassword);

		Timestamp now = Timestamp.from(Instant.now());
		user.setPasswordChangeDate(now);

		userDao.saveUserChanges(user);
	}

	private void encodeAndUpdateUserPassword(User user, String password) {
		String encodedPassword = passwordEncoder.encode(password);
		user.setPassword(encodedPassword);
	}

	@Override
	public void saveUserChanges(User user) throws BadRequestException {
		try {
			userDao.saveUserChanges(user);
		} catch (EntityExistsException | DataIntegrityViolationException e) {
			throw new BadRequestException("a user with the same username or email already exists");
		}
	}
}
