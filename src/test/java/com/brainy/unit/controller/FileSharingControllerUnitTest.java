package com.brainy.unit.controller;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.brainy.TestUtils;
import com.brainy.controller.FileSharingController;
import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;
import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UpdateSharedFileAccessRequest;
import com.brainy.service.UserFilesService;

public class FileSharingControllerUnitTest {

	private UserFilesService userFilesService;
	private FileSharingController fileSharingController;

	public FileSharingControllerUnitTest() {
		userFilesService = Mockito.mock();
		fileSharingController = new FileSharingController(userFilesService);
	}

	@Test
	public void shouldUpdateSharedFileAccess() throws BadRequestException {
		// Arrange

		User user = TestUtils.generateRandomUser();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateRandomUsername();
		UpdateSharedFileAccessRequest request = new UpdateSharedFileAccessRequest(true);

		// Act

		fileSharingController.updateSharedFileAccess(user, filename, sharedWithUsername, request);

		// Assert

		Mockito.verify(userFilesService).updateSharedFileAccess(user, filename, sharedWithUsername,
				request);
	}

	@Test
	public void shouldGetFilesSharedWithUser() {
		// Arrange

		User user = TestUtils.generateRandomUser();
		List<SharedFile> list = new ArrayList<>();

		Mockito.when(userFilesService.getFilesSharedWithUser(user)).thenReturn(list);

		// Act

		Response<List<SharedFile>> response = fileSharingController.getFilesSharedWithUser(user);

		// Assert

		Mockito.verify(userFilesService).getFilesSharedWithUser(user);
		Assertions.assertEquals(list, response.getData());
	}

	@Test
	public void shouldGetFileShares() {
		// Arrange

		User user = TestUtils.generateRandomUser();
		List<SharedFile> list = new ArrayList<>();
		String filename = TestUtils.generateRandomFilename();

		Mockito.when(userFilesService.getFileShares(user, filename)).thenReturn(list);

		// Act

		Response<List<SharedFile>> response = fileSharingController.getFileShares(user, filename);

		// Assert

		Mockito.verify(userFilesService).getFileShares(user, filename);
		Assertions.assertEquals(list, response.getData());
	}

	@Test
	public void shouldShareFile() throws BadRequestException {
		// Arrange

		User user = TestUtils.generateRandomUser();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateRandomUsername();

		// Act

		fileSharingController.shareFileWith(user, filename, sharedWithUsername, false);

		// Assert

		Mockito.verify(userFilesService).shareFileWith(user, filename, sharedWithUsername, false);
	}

	@Test
	public void shouldDeleteFileShare() throws BadRequestException {
		// Arrange

		User user = TestUtils.generateRandomUser();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateRandomUsername();

		// Act

		fileSharingController.deleteShare(user, filename, sharedWithUsername);

		// Assert

		Mockito.verify(userFilesService).deleteShare(user, filename, sharedWithUsername);
	}

	@Test
	public void shouldGetSharedFileContent() throws BadRequestException {
		// Arrange

		User sharedWithUser = TestUtils.generateRandomUser();
		String filename = TestUtils.generateRandomFilename();
		String fileOwnerUsername = TestUtils.generateRandomUsername();
		String fileContent = "[1, 2, 3]";

		Mockito.when(userFilesService.getSharedFileContent(fileOwnerUsername, filename,
				sharedWithUser.getUsername())).thenReturn(fileContent);

		// Act

		Response<String> actual = fileSharingController.getSharedFileContent(sharedWithUser,
				filename, fileOwnerUsername);

		// Assert

		Assertions.assertEquals(ResponseStatus.SUCCESS, actual.getStatus());
		Assertions.assertEquals(fileContent, actual.getData());
	}
}
