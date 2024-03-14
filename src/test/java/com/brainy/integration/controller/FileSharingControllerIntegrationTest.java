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
import com.brainy.integration.model.wrapper.ResponseListSharedFile;
import com.brainy.integration.model.wrapper.ResponseString;
import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.request.UpdateSharedFileAccessRequest;
import com.brainy.util.JsonUtil;

public class FileSharingControllerIntegrationTest extends IntegrationTest {

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
				"/api/file-shares?filename=" + filename, ResponseListSharedFile.class);

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

		String deleteShareUrl = String.format("/api/share?filename=%s&shared-with=%s", filename,
				sharedWithUser.getUsername());

		// Act

		getAuthenticatedRequest().delete(deleteShareUrl);

		ResponseEntity<ResponseListSharedFile> response = getAuthenticatedRequest().getForEntity(
				"/api/file-shares?filename=" + filename, ResponseListSharedFile.class);
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
				.getForEntity("/api/shared-with-me", ResponseListSharedFile.class);
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

		String updateShareUrl = String.format("/api/share?filename=%s&shared-with=%s", filename,
				sharedWithUser.getUsername());

		// Act

		getAuthenticatedRequest().patchForObject(updateShareUrl, requestBody, ResponseString.class);

		ResponseEntity<ResponseListSharedFile> response = getAuthenticatedRequest().getForEntity(
				"/api/file-shares?filename=" + filename, ResponseListSharedFile.class);

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

		String url = String.format("/api/share?filename=%s&file-owner=%s", filename,
				testUser.getUsername());

		String expected = JsonUtil.compressJson(fileContent);

		// Act

		ResponseEntity<ResponseString> response = IntegrationTestUtils
				.getAuthenticatedRestTemplateForUser(restTemplate, sharedWithUser)
				.getForEntity(url, ResponseString.class);

		ResponseString responseBody = response.getBody();

		// Assert

		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(responseBody);
		Assertions.assertNotNull(responseBody.getData());
		Assertions.assertEquals(expected, responseBody.getData());
	}

}
