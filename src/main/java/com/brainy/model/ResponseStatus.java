package com.brainy.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseStatus {
	SUCCESS("success"), ERROR("error"), BAD_REQUEST("bad request"), UNAUTHORIZED(
			"unauthorized"), NOT_FOUND("not found");

	private String statusString;

	ResponseStatus(String statusString) {
		this.statusString = statusString;
	}

	@JsonValue
	public String getStatusString() {
		return statusString;
	}
}
