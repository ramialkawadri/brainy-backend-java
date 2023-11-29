package com.brainy.model.exception;

import com.brainy.model.ResponseStatus;

public class FileDoesNotExistException extends RequestException {

	public FileDoesNotExistException(String filename) {
		super(filename + " does not exist!", ResponseStatus.NOT_FOUND);
	}
}
