package com.brainy.unit.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import com.brainy.controller.GlobalExceptionHandler;
import com.brainy.model.Response;

/**
 * This class just contains edge cases that are not handled by the integration test
 */
public class GlobalExceptionHandlerUnitTest {

	@Test
	public void shouldHandleNullFieldErrorOnInvalidArgumentException() {
		// Arrange

		GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
		MethodArgumentNotValidException exception = Mockito.mock();
		Mockito.when(exception.getFieldError()).thenReturn(null);

		// Act

		ResponseEntity<Response<String>> actual =
				globalExceptionHandler.handleMethodArgumentNotValid(exception);

		// Assert

		Assertions.assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
	}
}
