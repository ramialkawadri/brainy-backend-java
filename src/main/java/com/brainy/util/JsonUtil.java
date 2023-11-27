package com.brainy.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.common.lang.Nullable;

public class JsonUtil {

	/**
	 * Returns null if json is invalid.
	 */
	@Nullable
	public static String compressJson(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
			mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
			return mapper.readTree(json).toString();
		} catch (JsonProcessingException e) {
			return null;
		}
	}
}
