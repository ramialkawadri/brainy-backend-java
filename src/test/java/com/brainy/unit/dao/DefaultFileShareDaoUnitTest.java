package com.brainy.unit.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.brainy.TestUtils;
import com.brainy.dao.DefaultFileShareDao;
import com.brainy.dao.FileShareDao;
import com.brainy.dao.UserDao;
import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UpdateSharedFileAccessRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class DefaultFileShareDaoUnitTest {

	private UserDao userDao;
	private EntityManager entityManager;
	private FileShareDao fileShareDao;

	public DefaultFileShareDaoUnitTest() {
		userDao = Mockito.mock();
		entityManager = Mockito.mock();
		fileShareDao = new DefaultFileShareDao(entityManager, userDao);
	}

	@Test
	public void shouldGetFilesSharedWithUser() {
		// Arrange
		User user = TestUtils.generateRandomUser();
		TypedQuery<Object> query = Mockito.mock();

		Mockito.when(entityManager.createQuery(Mockito.any(), Mockito.any())).thenReturn(query);

		Mockito.when(query.setParameter(Mockito.anyString(), Mockito.any(User.class)))
				.thenReturn(query);

		// Act
		fileShareDao.getFilesSharedWithUser(user);

		// Assert
		Mockito.verify(query).getResultList();
	}

	@Test
	public void shouldGetFileShares() {
		// Arrange
		User user = TestUtils.generateRandomUser();
		TypedQuery<Object> query = Mockito.mock();
		String filename = TestUtils.generateRandomFilename();

		Mockito.when(entityManager.createQuery(Mockito.any(), Mockito.any())).thenReturn(query);

		Mockito.when(query.setParameter(Mockito.anyString(), Mockito.any(User.class)))
				.thenReturn(query);

		Mockito.when(query.setParameter(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(query);

		// Act
		fileShareDao.getFileShares(user, filename);

		// Assert
		Mockito.verify(query).getResultList();
	}

	@Test
	public void shouldReturnTrueOnIsFileSharedWith() {
		// Arrange
		String fileOwnerUsername = TestUtils.generateUniqueUsername();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateUniqueUsername();
		SharedFile sharedFile = new SharedFile();

		setEntityMangerSingleSharedFileResult(sharedFile);

		// Act
		boolean returnValue =
				fileShareDao.isFileSharedWith(fileOwnerUsername, filename, sharedWithUsername);

		// Assert
		Assertions.assertTrue(returnValue);
	}

	@Test
	public void shouldReturnFalseOnIsFileSharedWith() {
		// Arrange
		String fileOwnerUsername = TestUtils.generateUniqueUsername();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateUniqueUsername();

		setEntityMangerSingleSharedFileResult(null);

		// Act
		boolean returnValue =
				fileShareDao.isFileSharedWith(fileOwnerUsername, filename, sharedWithUsername);

		// Assert
		Assertions.assertFalse(returnValue);
	}

	@Test
	public void shouldShareFile() throws BadRequestException {
		// Arrange
		User fileOwner = TestUtils.generateRandomUser();
		String filename = TestUtils.generateRandomFilename();
		User sharedWith = TestUtils.generateRandomUser();
		boolean canEdit = false;

		Mockito.when(userDao.findUserByUsername(fileOwner.getUsername())).thenReturn(fileOwner);

		Mockito.when(userDao.findUserByUsername(sharedWith.getUsername())).thenReturn(sharedWith);

		// Act
		fileShareDao.shareFile(fileOwner.getUsername(), filename, sharedWith.getUsername(),
				canEdit);

		// Assert
		Mockito.verify(entityManager).persist(Mockito.any());
	}

	@Test
	public void shouldNotShareFileBecauseSharedWithUserDoesNotExist() {
		// Arrange
		User fileOwner = TestUtils.generateRandomUser();
		String filename = TestUtils.generateRandomFilename();
		User sharedWith = TestUtils.generateRandomUser();
		boolean canEdit = false;

		Mockito.when(userDao.findUserByUsername(fileOwner.getUsername())).thenReturn(fileOwner);

		Mockito.when(userDao.findUserByUsername(sharedWith.getUsername())).thenReturn(null);

		// Act & Assert
		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			fileShareDao.shareFile(fileOwner.getUsername(), filename, sharedWith.getUsername(),
					canEdit);
		});
	}

	@Test
	public void shouldDeleteFileShare() {
		// Arrange
		String fileOwnerUsername = TestUtils.generateUniqueUsername();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateUniqueUsername();
		SharedFile sharedFile = new SharedFile();

		setEntityMangerSingleSharedFileResult(sharedFile);

		// Act
		fileShareDao.deleteFileShare(fileOwnerUsername, filename, sharedWithUsername);

		// Assert
		Mockito.verify(entityManager).remove(sharedFile);
	}

	@Test
	public void shouldUpdateSharedFileAccess() {
		// Arrange
		String fileOwnerUsername = TestUtils.generateUniqueUsername();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateUniqueUsername();
		UpdateSharedFileAccessRequest request = new UpdateSharedFileAccessRequest(true);
		SharedFile sharedFile = Mockito.mock();

		setEntityMangerSingleSharedFileResult(sharedFile);

		// Act
		fileShareDao.updateSharedFileAccess(fileOwnerUsername, filename, sharedWithUsername,
				request);

		// Assert
		Mockito.verify(sharedFile).setCanEdit(request.canEdit());
		Mockito.verify(entityManager).merge(sharedFile);
	}

	private void setEntityMangerSingleSharedFileResult(SharedFile sharedFile) {
		TypedQuery<Object> query = Mockito.mock();

		Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.any()))
				.thenReturn(query);

		Mockito.when(query.setParameter(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(query);

		Mockito.when(query.getSingleResult()).thenReturn(sharedFile);
	}
}
