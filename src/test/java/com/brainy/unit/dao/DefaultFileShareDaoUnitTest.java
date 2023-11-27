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
		User user = TestUtils.generateRandomUser();
		TypedQuery<Object> query = Mockito.mock();

		Mockito.when(entityManager.createQuery(Mockito.any(), Mockito.any())).thenReturn(query);

		Mockito.when(query.setParameter(Mockito.anyString(), Mockito.any(User.class)))
				.thenReturn(query);

		fileShareDao.getFilesSharedWithUser(user);

		Mockito.verify(query).getResultList();
	}

	@Test
	public void shouldGetFileShares() {
		User user = TestUtils.generateRandomUser();
		TypedQuery<Object> query = Mockito.mock();
		String filename = TestUtils.generateRandomFilename();

		Mockito.when(entityManager.createQuery(Mockito.any(), Mockito.any())).thenReturn(query);

		Mockito.when(query.setParameter(Mockito.anyString(), Mockito.any(User.class)))
				.thenReturn(query);

		Mockito.when(query.setParameter(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(query);

		fileShareDao.getFileShares(user, filename);

		Mockito.verify(query).getResultList();
	}

	@Test
	public void shouldReturnTrueOnIsFileSharedWith() {
		String fileOwnerUsername = TestUtils.generateUniqueUsername();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateUniqueUsername();
		SharedFile sharedFile = new SharedFile();

		setEntityMangerSingleSharedFileResult(sharedFile);

		boolean returnValue =
				fileShareDao.isFileSharedWith(fileOwnerUsername, filename, sharedWithUsername);

		Assertions.assertTrue(returnValue);
	}

	@Test
	public void shouldReturnFalseOnIsFileSharedWith() {
		String fileOwnerUsername = TestUtils.generateUniqueUsername();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateUniqueUsername();

		setEntityMangerSingleSharedFileResult(null);

		boolean returnValue =
				fileShareDao.isFileSharedWith(fileOwnerUsername, filename, sharedWithUsername);

		Assertions.assertFalse(returnValue);
	}

	@Test
	public void shouldShareFile() throws BadRequestException {
		User fileOwner = TestUtils.generateRandomUser();
		String filename = TestUtils.generateRandomFilename();
		User sharedWith = TestUtils.generateRandomUser();
		boolean canEdit = false;

		Mockito.when(userDao.findUserByUserName(fileOwner.getUsername())).thenReturn(fileOwner);

		Mockito.when(userDao.findUserByUserName(sharedWith.getUsername())).thenReturn(sharedWith);

		fileShareDao.shareFile(fileOwner.getUsername(), filename, sharedWith.getUsername(),
				canEdit);

		Mockito.verify(entityManager).persist(Mockito.any());
	}

	@Test
	public void shouldNotShareFileBecauseSharedWithUserDoesNotExist() {
		User fileOwner = TestUtils.generateRandomUser();
		String filename = TestUtils.generateRandomFilename();
		User sharedWith = TestUtils.generateRandomUser();
		boolean canEdit = false;

		Mockito.when(userDao.findUserByUserName(fileOwner.getUsername())).thenReturn(fileOwner);

		Mockito.when(userDao.findUserByUserName(sharedWith.getUsername())).thenReturn(null);

		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			fileShareDao.shareFile(fileOwner.getUsername(), filename, sharedWith.getUsername(),
					canEdit);
		});
	}

	@Test
	public void shouldDeleteFileShare() {
		String fileOwnerUsername = TestUtils.generateUniqueUsername();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateUniqueUsername();
		SharedFile sharedFile = new SharedFile();

		setEntityMangerSingleSharedFileResult(sharedFile);

		fileShareDao.deleteFileShare(fileOwnerUsername, filename, sharedWithUsername);

		Mockito.verify(entityManager).remove(sharedFile);
	}

	@Test
	public void shouldUpdateSharedFileAccess() {
		String fileOwnerUsername = TestUtils.generateUniqueUsername();
		String filename = TestUtils.generateRandomFilename();
		String sharedWithUsername = TestUtils.generateUniqueUsername();
		UpdateSharedFileAccessRequest request = new UpdateSharedFileAccessRequest(true);
		SharedFile sharedFile = Mockito.mock();

		setEntityMangerSingleSharedFileResult(sharedFile);

		fileShareDao.updateSharedFileAccess(fileOwnerUsername, filename, sharedWithUsername,
				request);

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
