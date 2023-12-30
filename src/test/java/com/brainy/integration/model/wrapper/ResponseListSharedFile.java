package com.brainy.integration.model.wrapper;

import java.util.List;
import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;
import com.brainy.model.entity.SharedFile;

public class ResponseListSharedFile extends Response<List<SharedFile>> {
	public ResponseListSharedFile(List<SharedFile> data, ResponseStatus status) {
		super(data, status);
	}
}
