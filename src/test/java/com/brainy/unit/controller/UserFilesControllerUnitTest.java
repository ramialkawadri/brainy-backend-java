package com.brainy.unit.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.brainy.TestUtils;
import com.brainy.controller.UserFilesController;
import com.brainy.model.Response;
import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.exception.FileDoesNotExistException;
import com.brainy.model.request.UpdateSharedFileAccessRequest;
import com.brainy.service.UserFilesService;

public class UserFilesControllerUnitTest {

	private UserFilesService userFilesService;
	private UserFilesController userFilesController;

	public UserFilesControllerUnitTest() {
		userFilesService = Mockito.mock();
		userFilesController = new UserFilesController(userFilesService);
	}

	@Test
	public void shouldReturnUserFiles() throws FileDoesNotExistException {
		// Arrange
		List<String> userFiles = new ArrayList<>();
		userFiles.add(TestUtils.generateRandomFilename());
		userFiles.add(TestUtils.generateRandomFilename());

		User user = TestUtils.generateRandomUser();
		Mockito.when(userFilesService.getUserFiles(user.getUsername())).thenReturn(userFiles);

		// Act
		Response<Object> returnValue = userFilesController.getFileContentOrUserFiles(user, null);

		// Assert
		Mockito.verify(userFilesService).getUserFiles(user.getUsername());
		Assertions.assertEquals(userFiles, returnValue.getData());
	}

	@Test
	public void shouldReturnFileContent() throws FileDoesNotExistException {
		// Arrange
		String fileContent = TestUtils.generateRandomFileContent();
		String filename = TestUtils.generateRandomFilename();
		User user = TestUtils.generateRandomUser();

		Mockito.when(userFilesService.getFileContent(user.getUsername(), filename))
				.thenReturn(fileContent);

		// Act
		Response<Object> returnValue =
				userFilesController.getFileContentOrUserFiles(user, filename);

		// Assert
		Mockito.verify(userFilesService).getFileContent(user.getUsername(), filename);
		Assertions.assertEquals(fileContent, returnValue.getData());
	}

	@Test
	public void shouldCreateOrUpdateFile() throws BadRequestException {
		// Arrange
		String fileContent = TestUtils.generateRandomFileContent();
		String filename = TestUtils.generateRandomFilename();
		User user = TestUtils.generateRandomUser();

		Mockito.when(userFilesService.canUserCreateFileWithSize(user.getUsername(), filename,
				fileContent.length())).thenReturn(true);

		// Act
		userFilesController.createOrUpdateJsonFile(user, filename, fileContent);

		// Assert
		Mockito.verify(userFilesService).createOrUpdateJsonFile(user.getUsername(), filename,
				fileContent);
	}

	@Test
	public void shouldNotCreateOrUpdateFile() {
		// Arrange
		String fileContent = TestUtils.generateRandomFileContent();
		String filename = TestUtils.generateRandomFilename();
		User user = TestUtils.generateRandomUser();

		Mockito.when(userFilesService.canUserCreateFileWithSize(user.getUsername(), filename,
				fileContent.length())).thenReturn(false);

		// Act & Assert
		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			userFilesController.createOrUpdateJsonFile(user, filename, fileContent);
		});
	}

	@Test
	public void shouldDeleteFile() throws FileDoesNotExistException {
		// Arrange
		User user = TestUtils.generateRandomUser();
		String filename = TestUtils.generateRandomFilename();

		// Act
		userFilesController.deleteFile(user, filename);

		// Assert
		Mockito.verify(userFilesService).deleteFile(user.getUsername(), filename);
	}

	@Test
	public void shouldGetUserUsedStorage() {
		// Arrange
		User user = TestUtils.generateRandomUser();
		long usedStorage = 100000L;

		Mockito.when(userFilesService.getUserUsedStorage(user.getUsername()))
				.thenReturn(usedStorage);

		// Act
		long returnValue = userFilesController.getUserUsedStorage(user).getData();

		// Assert
		Mockito.verify(userFilesService).getUserUsedStorage(user.getUsername());
		Assertions.assertEquals(returnValue, usedStorage);
	}

	@Test
	public void shouldCreateFolder() throws BadRequestException {
		// Arrange
		User user = TestUtils.generateRandomUser();
		String foldername = TestUtils.generateRandomFilename();

		// Act
		userFilesController.createFolder(user, foldername);

		// Assert
		Mockito.verify(userFilesService).createFolder(user.getUsername(), foldername);
	}

	@Test
	public void shouldDeleteFolder() throws FileDoesNotExistException {
		// Arrange
		User user = TestUtils.generateRandomUser();
		String foldername = TestUtils.generateRandomFilename();

		// Act
		userFilesController.deleteFile(user, foldername);

		// Assert
		Mockito.verify(userFilesService).deleteFile(user.getUsername(), foldername);
	}

	@Test
	public void shouldGetFilesSharedWithUser() {
		// Arrange
		User user = TestUtils.generateRandomUser();
		List<SharedFile> list = new ArrayList<>();

		Mockito.when(userFilesService.getFilesSharedWithUser(user)).thenReturn(list);

		// Act
		Response<List<SharedFile>> response = userFilesController.getFilesSharedWithUser(user);

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
		Response<List<SharedFile>> response = userFilesController.getFileShares(user, filename);

		// Assert
		Mockito.verify(userFilesService).getFileShares(user, filename);
		Assertions.assertEquals(list, response.getData());
	}

	@Test
	public void shouldShareFile() throws BadRequestException {
		// Arrange
		User user = TestUtils.generateRandomUser();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateUniqueUsername();

		// Act
		userFilesController.shareFileWith(user, filename, sharedWithUsername, false);

		// Assert
		Mockito.verify(userFilesService).shareFileWith(user, filename, sharedWithUsername, false);
	}

	@Test
	public void shouldDeleteFileShare() throws BadRequestException {
		// Arrange
		User user = TestUtils.generateRandomUser();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateUniqueUsername();

		// Act
		userFilesController.deleteShare(user, filename, sharedWithUsername);

		// Assert
		Mockito.verify(userFilesService).deleteShare(user, filename, sharedWithUsername);
	}

	@Test
	public void shouldUpdateSharedFileAccess() throws BadRequestException {
		// Arrange
		User user = TestUtils.generateRandomUser();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateUniqueUsername();
		UpdateSharedFileAccessRequest request = new UpdateSharedFileAccessRequest(true);

		// Act
		userFilesController.updateSharedFileAccess(user, filename, sharedWithUsername, request);

		// Assert
		Mockito.verify(userFilesService).updateSharedFileAccess(user, filename, sharedWithUsername,
				request);
	}
}
