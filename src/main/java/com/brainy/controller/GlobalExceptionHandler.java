package com.brainy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;
import com.brainy.model.exception.RequestException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<Response<String>> requestExceptionHandler(RequestException e) {

		String responseString = e.getRequestExceptionTitle() + ": " + e.getMessage();

		Response<String> responseBody = new Response<String>(responseString, e.getResponseStatus());

		return new ResponseEntity<Response<String>>(responseBody, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	public ResponseEntity<Response<String>> handleMissingBody(HttpMessageNotReadableException e) {

		Response<String> response = new Response<>("missing body", ResponseStatus.BAD_REQUEST);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	public ResponseEntity<Response<String>> handleMethodArgumentNotValid(
			MethodArgumentNotValidException e) {

		FieldError fieldError = e.getFieldError();

		if (fieldError == null)
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

		String error = fieldError.getField() + ": " + fieldError.getDefaultMessage();

		Response<String> response = new Response<>(error, ResponseStatus.BAD_REQUEST);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// 404 response
	@ExceptionHandler
	public ResponseEntity<Response<String>> handleNoHandlerFound(NoHandlerFoundException e) {

		String errorMessage = e.getRequestURL() + " is an invalid endpoint!";

		Response<String> response = new Response<String>(errorMessage, ResponseStatus.NOT_FOUND);

		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler
	public ResponseEntity<Response<String>> handleMissingParameter(
			MissingServletRequestParameterException e) {

		String errorMessage = "the query string '" + e.getParameterName() + "' is missing!";

		Response<String> response = new Response<String>(errorMessage, ResponseStatus.BAD_REQUEST);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// Must be the last exception handler!
	@ExceptionHandler
	public ResponseEntity<Response<String>> exceptionHandler(Exception e) {
		e.printStackTrace();

		Response<String> responseBody =
				new Response<String>("error: an error has occurred", ResponseStatus.ERROR);

		return new ResponseEntity<Response<String>>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
