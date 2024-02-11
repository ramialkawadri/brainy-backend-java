package com.brainy.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseStatus {
	SUCCESS("success", HttpStatus.OK),
	ERROR("error", HttpStatus.INTERNAL_SERVER_ERROR),
	BAD_REQUEST("bad request", HttpStatus.BAD_REQUEST),
	UNAUTHORIZED("unauthorized", HttpStatus.UNAUTHORIZED),
	NOT_FOUND("not found", HttpStatus.NOT_FOUND);

	private String statusString;
	private HttpStatusCode httpStatusCode;

	ResponseStatus(String statusString, HttpStatusCode httpStatusCode) {
		this.statusString = statusString;
		this.httpStatusCode = httpStatusCode;
	}

	@JsonValue
	public String getStatusString() {
		return statusString;
	}

	public HttpStatusCode toHttpStatusCode() {
		return httpStatusCode;
	}
}
