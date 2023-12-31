package com.brainy.integration.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.brainy.TestUtils;
import com.brainy.integration.IntegrationTest;
import com.brainy.integration.IntegrationTestUtils;
import com.brainy.integration.model.wrapper.ResponseInteger;
import com.brainy.integration.model.wrapper.ResponseListSharedFile;
import com.brainy.integration.model.wrapper.ResponseString;
import com.brainy.integration.model.wrapper.ResponseStringList;
import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.request.UpdateSharedFileAccessRequest;
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

	@Test
	public void shouldUpdateFileContent() {
		// Arrange
		String filename = TestUtils.generateRandomFilename();
		String fileContent = TestUtils.generateRandomFileContent();
		String newFileContent = TestUtils.generateRandomFileContent();
		String expected = JsonUtil.compressJson(newFileContent);

		IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), filename, fileContent);

		// Act
		IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), filename, newFileContent);
		String actual = IntegrationTestUtils.getFileContent(getAuthenticatedRequest(), filename);

		// Assert
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
		getAuthenticatedRequest().delete("/api/files?filename=" + filename);

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
				getAuthenticatedRequest().getForEntity("/api/files/size", ResponseInteger.class);
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
		ResponseEntity<ResponseString> response = getAuthenticatedRequest().postForEntity(
				"/api/files/folder?foldername=" + foldername, null, ResponseString.class);

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
		getAuthenticatedRequest().delete("/api/files/folder?foldername=" + foldername);

		ResponseEntity<ResponseStringList> userFilesResponse =
				getAuthenticatedRequest().getForEntity("/api/files", ResponseStringList.class);
		ResponseStringList userFilesResponseBody = userFilesResponse.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.OK, userFilesResponse.getStatusCode());
		Assertions.assertNotNull(userFilesResponseBody);
		Assertions.assertNotNull(userFilesResponseBody.getData());
		Assertions.assertEquals(0, userFilesResponseBody.getData().length);
	}

	@Test
	public void shouldShareAndGetFileShares() {
		// Arrange
		User sharedWithUser = TestUtils.generateRandomUser();
		IntegrationTestUtils.registerUser(restTemplate, sharedWithUser);

		String filename = TestUtils.generateRandomFilename();
		IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), filename, "{}");

		// Act
		IntegrationTestUtils.shareFile(getAuthenticatedRequest(), filename,
				sharedWithUser.getUsername(), true);

		ResponseEntity<ResponseListSharedFile> response = getAuthenticatedRequest().getForEntity(
				"/api/files/share?filename=" + filename, ResponseListSharedFile.class);

		ResponseListSharedFile sharedFiles = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(sharedFiles);
		Assertions.assertNotNull(sharedFiles.getData());
		Assertions.assertEquals(1, sharedFiles.getData().size());

		SharedFile sharedFile = sharedFiles.getData().get(0);
		Assertions.assertEquals(filename, sharedFile.getFilename());
		Assertions.assertEquals(true, sharedFile.canEdit());
		Assertions.assertEquals(testUser.getUsername(), sharedFile.getFileOwnerUsername());
		Assertions.assertEquals(sharedWithUser.getUsername(), sharedFile.getSharedWithUsername());
	}

	@Test
	public void shouldDeleteShare() {
		// Arrange
		User sharedWithUser = TestUtils.generateRandomUser();
		IntegrationTestUtils.registerUser(restTemplate, sharedWithUser);

		String filename = TestUtils.generateRandomFilename();
		IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), filename, "{}");
		IntegrationTestUtils.shareFile(getAuthenticatedRequest(), filename,
				sharedWithUser.getUsername(), true);

		String deleteShareUrl = String.format("/api/files/share?filename=%s&shared-with=%s",
				filename, sharedWithUser.getUsername());

		// Act
		getAuthenticatedRequest().delete(deleteShareUrl);

		ResponseEntity<ResponseListSharedFile> response = getAuthenticatedRequest().getForEntity(
				"/api/files/share?filename=" + filename, ResponseListSharedFile.class);
		ResponseListSharedFile sharedFiles = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(sharedFiles);
		Assertions.assertNotNull(sharedFiles.getData());
		Assertions.assertEquals(0, sharedFiles.getData().size());
	}

	@Test
	public void shouldGetFilesSharedWithMe() {
		// Arrange
		User sharedWithUser = TestUtils.generateRandomUser();
		IntegrationTestUtils.registerUser(restTemplate, sharedWithUser);
		TestRestTemplate authenticatedRestTemplateForSharedWithUser = IntegrationTestUtils
				.getAuthenticatedRestTemplateForUser(restTemplate, sharedWithUser);

		String filename = TestUtils.generateRandomFilename();
		IntegrationTestUtils.uploadFile(authenticatedRestTemplateForSharedWithUser, filename, "{}");

		// Act
		IntegrationTestUtils.shareFile(authenticatedRestTemplateForSharedWithUser, filename,
				testUser.getUsername(), true);

		ResponseEntity<ResponseListSharedFile> response = getAuthenticatedRequest()
				.getForEntity("/api/files/shared-with-me", ResponseListSharedFile.class);
		ResponseListSharedFile sharedFiles = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(sharedFiles);
		Assertions.assertNotNull(sharedFiles.getData());
		Assertions.assertEquals(1, sharedFiles.getData().size());
	}

	@Test
	public void shouldUpdateSharedFileAccess() {
		// Arrange
		User sharedWithUser = TestUtils.generateRandomUser();
		IntegrationTestUtils.registerUser(restTemplate, sharedWithUser);

		String filename = TestUtils.generateRandomFilename();
		IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), filename, "{}");
		IntegrationTestUtils.shareFile(getAuthenticatedRequest(), filename,
				sharedWithUser.getUsername(), true);

		HttpEntity<UpdateSharedFileAccessRequest> requestBody =
				new HttpEntity<>(new UpdateSharedFileAccessRequest(false));

		String updateShareUrl = String.format("/api/files/share?filename=%s&shared-with=%s",
				filename, sharedWithUser.getUsername());

		// Act
		getAuthenticatedRequest().patchForObject(updateShareUrl, requestBody, ResponseString.class);

		ResponseEntity<ResponseListSharedFile> response = getAuthenticatedRequest().getForEntity(
				"/api/files/share?filename=" + filename, ResponseListSharedFile.class);

		ResponseListSharedFile sharedFiles = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(sharedFiles);
		Assertions.assertNotNull(sharedFiles.getData());
		Assertions.assertEquals(1, sharedFiles.getData().size());

		SharedFile sharedFile = sharedFiles.getData().get(0);
		Assertions.assertEquals(filename, sharedFile.getFilename());
		Assertions.assertEquals(false, sharedFile.canEdit());
	}

	@Test
	public void shouldGetSharedFileContent() {
		// Arrange
		User sharedWithUser = TestUtils.generateRandomUser();
		IntegrationTestUtils.registerUser(restTemplate, sharedWithUser);

		String filename = TestUtils.generateRandomFilename();
		String fileContent = TestUtils.generateRandomFileContent();
		IntegrationTestUtils.uploadFile(getAuthenticatedRequest(), filename, fileContent);
		IntegrationTestUtils.shareFile(getAuthenticatedRequest(), filename,
				sharedWithUser.getUsername(), false);

		String url = String.format("/api/files/shared-file?filename=%s&file-owner=%s", filename,
				testUser.getUsername());

		// Act
		ResponseEntity<ResponseString> response = IntegrationTestUtils
				.getAuthenticatedRestTemplateForUser(restTemplate, sharedWithUser)
				.getForEntity(url, ResponseString.class);

		ResponseString responseBody = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(responseBody);
		Assertions.assertNotNull(responseBody.getData());
		Assertions.assertEquals(fileContent, responseBody.getData());
	}
}
