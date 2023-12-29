package com.brainy.integration.model.wrapper;

import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;

public class ResponseStringList extends Response<String[]> {
	public ResponseStringList(String[] data, ResponseStatus status) {
		super(data, status);
	}
}
