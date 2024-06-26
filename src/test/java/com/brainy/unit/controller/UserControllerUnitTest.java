package com.brainy.unit.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.brainy.TestUtils;
import com.brainy.controller.UserController;
import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;
import com.brainy.model.ResponseWithoutData;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UpdateUserRequest;
import com.brainy.service.UserService;

public class UserControllerUnitTest {

	private UserService userService;
	private UserController userController;

	public UserControllerUnitTest() {
		userService = Mockito.mock();
		userController = new UserController(userService);
	}

	@Test
	public void shouldReturnUserInformation() {
		// Arrange

		User user = TestUtils.generateRandomUser();

		// Act

		Response<User> response = userController.getUserInformation(user);

		// Assert

		Assertions.assertEquals(ResponseStatus.SUCCESS, response.getStatus());
		Assertions.assertEquals(user, response.getData());
	}

	@Test
	public void shouldUpdateUser() throws BadRequestException {
		// Arrange

		User user = TestUtils.generateRandomUser();
		UpdateUserRequest request = Mockito.mock();

		// Act

		ResponseWithoutData response = userController.updateUser(user, request);

		// Assert

		Mockito.verify(request).applyUpdatesOnUser(user);
		Mockito.verify(userService).saveUserChanges(user);
		Assertions.assertEquals(ResponseStatus.SUCCESS, response.getStatus());
	}
}
