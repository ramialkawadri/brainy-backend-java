package com.brainy.unit.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.brainy.TestUtils;
import com.brainy.controller.UserFilesController;
import com.brainy.model.Response;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.exception.FileDoesNotExistException;
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

		Response<List<String>> returnValue = userFilesController.getUserFiles(user);

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

		Response<String> returnValue = userFilesController.getFileContent(user, filename);

		// Assert

		Mockito.verify(userFilesService).getFileContent(user.getUsername(), filename);
		Assertions.assertEquals(fileContent, returnValue.getData());
	}

	@Test
	public void shouldCreateFile() throws BadRequestException {
		// Arrange

		String fileContent = TestUtils.generateRandomFileContent();
		String filename = TestUtils.generateRandomFilename();
		User user = TestUtils.generateRandomUser();

		Mockito.when(userFilesService.canUserCreateFileWithSize(user.getUsername(), filename,
				fileContent.length())).thenReturn(true);

		// Act

		userFilesController.createJsonFile(user, filename, fileContent);

		// Assert

		Mockito.verify(userFilesService).createOrUpdateJsonFile(user.getUsername(), filename,
				fileContent);
	}

	@Test
	public void shouldNotCreateFileWhenFilenameIsEmpty() {
		// Arrange

		User user = TestUtils.generateRandomUser();

		// Act & Assert

		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			userFilesController.createJsonFile(user, "", "");
		});
	}

	@Test
	public void shouldNotCreateFileBecauseOfNoRemainingSpace() {
		// Arrange

		String fileContent = TestUtils.generateRandomFileContent();
		String filename = TestUtils.generateRandomFilename();
		User user = TestUtils.generateRandomUser();

		Mockito.when(userFilesService.canUserCreateFileWithSize(user.getUsername(), filename,
				fileContent.length())).thenReturn(false);

		// Act & Assert

		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			userFilesController.createJsonFile(user, filename, fileContent);
		});
	}

	@Test
	public void shouldUpdateFile() throws BadRequestException {
		// Arrange

		String fileContent = TestUtils.generateRandomFileContent();
		String filename = TestUtils.generateRandomFilename();
		User user = TestUtils.generateRandomUser();

		Mockito.when(userFilesService.canUserCreateFileWithSize(user.getUsername(), filename,
				fileContent.length())).thenReturn(true);

		// Act

		userFilesController.updateJsonFile(user, filename, fileContent);

		// Assert

		Mockito.verify(userFilesService).createOrUpdateJsonFile(user.getUsername(), filename,
				fileContent);
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
	public void shouldNotCreateFolderWhenFolderNameIsEmpty() {
		// Arrange

		User user = TestUtils.generateRandomUser();

		// Act & Assert

		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			userFilesController.createFolder(user, "");
		});
	}

	@Test
	public void shouldDeleteFolder() throws BadRequestException, FileDoesNotExistException {
		// Arrange

		User user = TestUtils.generateRandomUser();
		String foldername = TestUtils.generateRandomFilename();

		// Act

		userFilesController.deleteFolder(user, foldername);

		// Assert

		Mockito.verify(userFilesService).deleteFolder(user.getUsername(), foldername);
	}

	@Test
	public void shouldNotDeleteFolderWhenFolderNameIsEmpty() {
		// Arrange

		User user = TestUtils.generateRandomUser();

		// Act & Assert

		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			userFilesController.deleteFolder(user, "");
		});
	}
}
