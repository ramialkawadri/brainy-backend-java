package com.brainy.unit.dao;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.brainy.BrainyApplication;
import com.brainy.TestUtils;
import com.brainy.dao.DefaultFileShareDao;
import com.brainy.dao.UserDao;
import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UpdateSharedFileAccessRequest;

@SpringBootTest(classes = BrainyApplication.class)
public class DefaultFileShareDaoUnitTest {

	@Autowired
	private UserDao userDao;

	@Autowired
	private DefaultFileShareDao fileShareDao;

	private User fileOwner;
	private User sharedWithUser;

	@BeforeEach
	public void setup() {
		fileOwner = TestUtils.generateRandomUser();
		sharedWithUser = TestUtils.generateRandomUser();

		userDao.registerUser(fileOwner);
		userDao.registerUser(sharedWithUser);
	}

	@Test
	public void shouldGetFilesSharedWithUser() throws BadRequestException {
		// Arrange
		int numberOfSharedFiles = 3; // A random number
		int numberOfUnsharedFiles = 2; // A random number

		User anotherUser = TestUtils.generateRandomUser();
		userDao.registerUser(anotherUser);

		for (int i = 0; i < numberOfSharedFiles; i++) {
			shareFile(TestUtils.generateRandomFilename());
		}

		for (int i = 0; i < numberOfUnsharedFiles; i++) {
			shareFile(TestUtils.generateRandomFilename(), anotherUser);
		}

		// Act
		List<SharedFile> actual = fileShareDao.getFilesSharedWithUser(sharedWithUser);

		// Assert
		Assertions.assertEquals(numberOfSharedFiles, actual.size());
	}

	@Test
	public void shouldGetFileShares() throws BadRequestException {
		// Arrange
		String filename = TestUtils.generateRandomFileContent();

		User[] users = new User[4];
		for (int i = 0; i < users.length; i++) {
			users[i] = TestUtils.generateRandomUser();
			userDao.registerUser(users[i]);
			shareFile(filename, users[i]);
		}

		// Act
		List<SharedFile> actual = fileShareDao.getFileShares(fileOwner, filename);

		// Assert
		Assertions.assertEquals(users.length, actual.size());

		for (SharedFile sharedFile : actual) {
			Assertions.assertEquals(filename, sharedFile.getFilename());
			Assertions.assertEquals(fileOwner, sharedFile.getFileOwner());
			Assertions.assertFalse(sharedFile.canEdit());

			boolean found = false;

			for (User user : users) {
				if (user.getUsername() == sharedFile.getSharedWithUsername()) {
					found = true;
					break;
				}
			}

			Assertions.assertTrue(found);
		}
	}

	@Test
	public void shouldReturnTrueOnIsFileSharedWith() throws BadRequestException {
		// Arrange
		String filename = TestUtils.generateRandomFilename();
		shareFile(filename);

		// Act
		boolean returnValue = fileShareDao.isFileSharedWith(fileOwner.getUsername(), filename,
				sharedWithUser.getUsername());

		// Assert
		Assertions.assertTrue(returnValue);
	}

	@Test
	public void shouldReturnFalseOnIsFileSharedWith() {
		// Arrange
		String filename = TestUtils.generateRandomFilename();

		// Act
		boolean returnValue = fileShareDao.isFileSharedWith(fileOwner.getUsername(), filename,
				sharedWithUser.getUsername());

		// Assert
		Assertions.assertFalse(returnValue);
	}

	@Test
	public void shouldShareFile() throws BadRequestException {
		// Arrange
		String filename = TestUtils.generateRandomFilename();

		// Act
		shareFile(filename);
		List<SharedFile> actual = fileShareDao.getFilesSharedWithUser(sharedWithUser);

		// Assert
		Assertions.assertEquals(1, actual.size());
	}

	@Test
	public void shouldNotShareFileBecauseSharedWithUserDoesNotExist() {
		// Arrange
		String filename = TestUtils.generateRandomFilename();
		User newUser = TestUtils.generateRandomUser();

		// Act & Assert
		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			shareFile(filename, newUser);
		});
	}

	@Test
	public void shouldDeleteFileShare() throws BadRequestException {
		// Arrange
		String filename = TestUtils.generateRandomFilename();
		shareFile(filename);

		// Act
		fileShareDao.deleteFileShare(fileOwner.getUsername(), filename,
				sharedWithUser.getUsername());

		boolean isShared = fileShareDao.isFileSharedWith(fileOwner.getUsername(), filename,
				sharedWithUser.getUsername());

		// Assert
		Assertions.assertFalse(isShared);
	}

	@Test
	public void shouldUpdateSharedFileAccess() throws BadRequestException {
		// Arrange
		String filename = TestUtils.generateRandomFilename();
		shareFile(filename, sharedWithUser, false);
		UpdateSharedFileAccessRequest request = new UpdateSharedFileAccessRequest(true);

		// Act
		fileShareDao.updateSharedFileAccess(fileOwner.getUsername(), filename,
				sharedWithUser.getUsername(), request);

		List<SharedFile> actual = fileShareDao.getFileShares(fileOwner, filename);

		// Assert
		Assertions.assertEquals(1, actual.size());
		Assertions.assertTrue(actual.get(0).canEdit());
	}

	private void shareFile(String filename) throws BadRequestException {
		shareFile(filename, sharedWithUser);
	}

	private void shareFile(String filename, User sharedWith) throws BadRequestException {
		shareFile(filename, sharedWith, false);
	}

	private void shareFile(String filename, User sharedWith, boolean canEdit)
			throws BadRequestException {
		fileShareDao.shareFile(fileOwner.getUsername(), filename, sharedWith.getUsername(),
				canEdit);
	}
}
