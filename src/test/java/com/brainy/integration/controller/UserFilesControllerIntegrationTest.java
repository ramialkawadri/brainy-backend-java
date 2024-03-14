package com.brainy.integration.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.brainy.TestUtils;
import com.brainy.integration.IntegrationTest;
import com.brainy.integration.IntegrationTestUtils;
import com.brainy.integration.model.wrapper.ResponseInteger;
import com.brainy.integration.model.wrapper.ResponseString;
import com.brainy.integration.model.wrapper.ResponseStringList;
import com.brainy.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class UserFilesControllerIntegrationTest extends IntegrationTest {

	@Test
	public void shouldCreateAndReadFile() {
		// Arrange

		String filename = TestUtils.generateRandomFilename();
		String fileContent = TestUtils.generateRandomFileContent();
		String expected = JsonUtil.compressJson(fileContent);

		// Act

		IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), filename, fileContent);
		String actual = IntegrationTestUtils.getFileContent(getAuthenticatedRequest(), filename);

		// Assert

		Assertions.assertEquals(expected, actual);
	}

	@SuppressWarnings("null")
	@Test
	public void shouldUpdateFileContent() {
		// Arrange

		String filename = TestUtils.generateRandomFilename();
		String fileContent = TestUtils.generateRandomFileContent();
		String newFileContent = TestUtils.generateRandomFileContent();
		String expected = JsonUtil.compressJson(newFileContent);

		// Act

		IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), filename, fileContent);

		ResponseEntity<Void> response =
				getAuthenticatedRequest().exchange("/api/file?filename=" + filename, HttpMethod.PUT,
						new HttpEntity<>(newFileContent), Void.class);
		String actual = IntegrationTestUtils.getFileContent(getAuthenticatedRequest(), filename);

		// Assert

		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void shouldGetUserFiles() throws JsonMappingException, JsonProcessingException {
		// Arrange

		String[] filenames =
				{TestUtils.generateRandomFilename(), TestUtils.generateRandomFilename()};

		for (String filename : filenames)
			IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), filename, "{}");

		// Act

		ResponseEntity<ResponseStringList> response =
				getAuthenticatedRequest().getForEntity("/api/files", ResponseStringList.class);
		ResponseStringList responseBody = response.getBody();

		// Assert

		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(responseBody);
		Assertions.assertNotNull(responseBody.getData());

		String[] actual = responseBody.getData();

		Assertions.assertEquals(filenames.length, actual.length);

		for (String filename : filenames) {
			boolean found = false;

			for (String actualFilename : actual)
				found = found || actualFilename.equals(filename);

			Assertions.assertTrue(found);
		}
	}

	@Test
	public void shouldDeleteFile() {
		// Arrange

		String filename = TestUtils.generateRandomFilename();
		String fileContent = "{}";

		IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), filename, fileContent);

		// Act

		getAuthenticatedRequest().delete("/api/file?filename=" + filename);

		ResponseEntity<ResponseStringList> response =
				getAuthenticatedRequest().getForEntity("/api/files", ResponseStringList.class);
		ResponseStringList responseBody = response.getBody();

		// Assert

		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(responseBody);
		Assertions.assertNotNull(responseBody.getData());
		Assertions.assertEquals(0, responseBody.getData().length);
	}

	@Test
	public void shouldGetUserUsedStorage() {
		// Arrange

		String filename = TestUtils.generateRandomFilename();
		String fileContent = TestUtils.generateRandomFileContent();
		String compressedFileContent = JsonUtil.compressJson(fileContent);

		IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), filename, fileContent);

		// Act

		ResponseEntity<ResponseInteger> response =
				getAuthenticatedRequest().getForEntity("/api/used-storage", ResponseInteger.class);
		ResponseInteger responseBody = response.getBody();

		// Assert

		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(responseBody);
		Assertions.assertNotNull(responseBody.getData());
		Assertions.assertEquals(compressedFileContent.getBytes().length, responseBody.getData());
	}

	@Test
	public void shouldCreateFolder() {
		// Arrange

		String foldername = TestUtils.generateRandomFilename();

		// Act

		ResponseEntity<ResponseString> response = getAuthenticatedRequest()
				.postForEntity("/api/folder?foldername=" + foldername, null, ResponseString.class);

		ResponseEntity<ResponseStringList> userFilesResponse =
				getAuthenticatedRequest().getForEntity("/api/files", ResponseStringList.class);
		ResponseStringList userFilesResponseBody = userFilesResponse.getBody();

		// Assert

		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals(HttpStatus.OK, userFilesResponse.getStatusCode());
		Assertions.assertNotNull(userFilesResponseBody);
		Assertions.assertNotNull(userFilesResponseBody.getData());
		Assertions.assertEquals(1, userFilesResponseBody.getData().length);
		Assertions.assertEquals(foldername + "/.hidden", userFilesResponseBody.getData()[0]);
	}

	@Test
	public void shouldDeleteFolder() {
		// Arrange

		String foldername = TestUtils.generateRandomFilename();

		IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), foldername + "/file1", "{}");
		IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), foldername + "/file2", "{}");

		// Act

		getAuthenticatedRequest().delete("/api/folder?foldername=" + foldername);

		ResponseEntity<ResponseStringList> userFilesResponse =
				getAuthenticatedRequest().getForEntity("/api/files", ResponseStringList.class);
		ResponseStringList userFilesResponseBody = userFilesResponse.getBody();

		// Assert

		Assertions.assertEquals(HttpStatus.OK, userFilesResponse.getStatusCode());
		Assertions.assertNotNull(userFilesResponseBody);
		Assertions.assertNotNull(userFilesResponseBody.getData());
		Assertions.assertEquals(0, userFilesResponseBody.getData().length);
	}
}
