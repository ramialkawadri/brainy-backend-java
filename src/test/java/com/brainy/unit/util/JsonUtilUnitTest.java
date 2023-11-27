package com.brainy.unit.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.brainy.util.JsonUtil;

public class JsonUtilUnitTest {

	@Test
	public void shouldCompressJson() {
		// Arrange
		String uncompressedJson = "   {\"isJson\"   :  true}";
		String compressedJson = uncompressedJson.replace(" ", "");

		// Act
		String returnValue = JsonUtil.compressJson(uncompressedJson);

		// Assert
		Assertions.assertEquals(compressedJson, returnValue);
	}

	@Test
	public void shouldNotAcceptInvalidJson() {
		// Arrange
		String json = "   aa{ \"isJson\": false}";

		// Act
		String returnValue = JsonUtil.compressJson(json);

		// Assert
		Assertions.assertNull(returnValue);
	}
}
