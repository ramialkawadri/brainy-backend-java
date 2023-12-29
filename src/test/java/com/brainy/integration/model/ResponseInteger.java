package com.brainy.integration.model;

import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;

public class ResponseInteger extends Response<Integer> {
	public ResponseInteger(Integer data, ResponseStatus status) {
		super(data, status);
	}
}
