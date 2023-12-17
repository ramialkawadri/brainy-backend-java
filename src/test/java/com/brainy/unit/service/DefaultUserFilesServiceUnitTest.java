package com.brainy.unit.service;

import static org.mockito.ArgumentMatchers.argThat;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.batch.BlobBatchClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobItemProperties;
import com.brainy.TestUtils;
import com.brainy.dao.FileShareDao;
import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.exception.FileDoesNotExistException;
import com.brainy.model.request.UpdateSharedFileAccessRequest;
import com.brainy.service.DefaultUserFilesService;
import com.brainy.service.UserFilesService;
import com.brainy.util.JsonUtil;

public class DefaultUserFilesServiceUnitTest {

	private BlobServiceClient blobServiceClient;
	private BlobContainerClient blobContainerClient;
	private BlobItem defaultIteratorBlobItem;
	private BlobClient blobClient;
	private long maxStoragePerUser = 500;
	private long maxFileSize = 100;
	private BlobBatchClient blobBatchClient;
	private FileShareDao fileShareDAO;
	private UserFilesService userFilesService;
	private String filename;
	private User fileOwner;

	@BeforeEach
	public void setup() {
		blobServiceClient = Mockito.mock();
		blobContainerClient = Mockito.mock();
		blobBatchClient = Mockito.mock();
		blobClient = Mockito.mock();
		fileShareDAO = Mockito.mock();

		filename = TestUtils.generateRandomFilename();
		fileOwner = TestUtils.generateRandomUser();

		Mockito.when(blobContainerClient.getBlobClient(filename)).thenReturn(blobClient);
		Mockito.when(blobServiceClient.createBlobContainerIfNotExists(fileOwner.getUsername()))
				.thenReturn(blobContainerClient);

		setupIterator();

		userFilesService = new DefaultUserFilesService(blobServiceClient, blobBatchClient,
				fileShareDAO, maxStoragePerUser, maxFileSize);
	}

	private void setupIterator() {
		PagedIterable<BlobItem> pagedIterable = Mockito.mock();
		defaultIteratorBlobItem = Mockito.mock();

		Mockito.when(blobContainerClient.listBlobs()).thenReturn(pagedIterable);
		Mockito.when(pagedIterable.iterator())
				.thenAnswer(a -> List.of(defaultIteratorBlobItem).iterator());
	}

	@Test
	public void shouldGetUserFiles() {
		// Arrange
		Mockito.when(defaultIteratorBlobItem.getName()).thenReturn("test");

		// Act
		List<String> userFiles = userFilesService.getUserFiles(fileOwner.getUsername());

		// Assert
		Mockito.verify(blobContainerClient).listBlobs();
		Assertions.assertEquals("test", userFiles.get(0));
	}

	@Test
	public void shouldGetFileContent() throws FileDoesNotExistException {
		// Arrange
		String fileContent = TestUtils.generateRandomFileContent();

		Mockito.when(blobClient.exists()).thenReturn(true);
		Mockito.when(blobClient.downloadContent())
				.thenReturn(BinaryData.fromBytes(fileContent.getBytes()));

		// Act
		String returnValue = userFilesService.getFileContent(fileOwner.getUsername(), filename);

		// Assert
		Assertions.assertEquals(fileContent, returnValue);
	}

	@Test
	public void shouldThrowExceptionWhenGettingNonExistingFileContent() {
		// Arrange
		String fileContent = TestUtils.generateRandomFileContent();

		Mockito.when(blobClient.exists()).thenReturn(false);
		Mockito.when(blobClient.downloadContent())
				.thenReturn(BinaryData.fromBytes(fileContent.getBytes()));

		// Act & Assert
		Assertions.assertThrowsExactly(FileDoesNotExistException.class, () -> {
			userFilesService.getFileContent(fileOwner.getUsername(), filename);
		});
	}

	@Test
	public void shouldCreateOrUpdateJsonFile() throws BadRequestException {
		// Arrange
		String jsonContent = "{ \"isJson\": true }";

		// Act
		userFilesService.createOrUpdateJsonFile(fileOwner.getUsername(), filename, jsonContent);

		// Assert
		Mockito.verify(blobClient).deleteIfExists();
		Mockito.verify(blobClient).upload(Mockito.argThat((ArgumentMatcher<BinaryData>) json -> {
			return json.toString().equals(JsonUtil.compressJson(jsonContent));
		}));
	}

	@Test
	public void shouldNotAcceptInvalidJson() {
		// Arrange
		String invalidJson = "aa { \"isJson\": true }";

		// Act & Assert
		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			userFilesService.createOrUpdateJsonFile(fileOwner.getUsername(), filename, invalidJson);
		});
	}

	@Test
	public void shouldDeleteFile() throws FileDoesNotExistException {
		// Arrange
		Mockito.when(blobClient.exists()).thenReturn(true);

		// Act
		userFilesService.deleteFile(fileOwner.getUsername(), filename);

		// Assert
		Mockito.verify(blobClient).deleteIfExists();
	}

	@Test
	public void shouldThrowExceptionWhenDeletingNonExistingFile() {
		// Arrange
		Mockito.when(blobClient.exists()).thenReturn(false);

		// Act & Assert
		Assertions.assertThrowsExactly(FileDoesNotExistException.class, () -> {
			userFilesService.deleteFile(fileOwner.getUsername(), filename);
		});
	}

	@Test
	public void shouldReturnTrueOnCanUserCreateFileWithSize() {
		// Arrange
		BlobItemProperties itemProperties = Mockito.mock();

		Mockito.when(defaultIteratorBlobItem.getProperties()).thenReturn(itemProperties);

		Mockito.when(itemProperties.getContentLength()).thenReturn(maxFileSize);

		// Act
		boolean returnValue =
				userFilesService.canUserCreateFileWithSize(fileOwner.getUsername(), filename, 1);

		// Assert
		Assertions.assertTrue(returnValue);
	}

	@Test
	public void shouldReturnFalseOnCanUserCreateFileWithSize() {
		// Arrange
		BlobItemProperties itemProperties = Mockito.mock();

		Mockito.when(defaultIteratorBlobItem.getProperties()).thenReturn(itemProperties);

		// Act
		boolean returnValue = userFilesService.canUserCreateFileWithSize(fileOwner.getUsername(),
				filename, maxFileSize + 1);

		// Act
		Assertions.assertFalse(returnValue);
	}

	@Test
	public void shouldGetUserUsedStorage() {
		// Arrange
		BlobItemProperties itemProperties = Mockito.mock();

		Mockito.when(defaultIteratorBlobItem.getProperties()).thenReturn(itemProperties);

		Mockito.when(itemProperties.getContentLength()).thenReturn(99L);

		// Act
		long returnValue = userFilesService.getUserUsedStorage(fileOwner.getUsername());

		// Assert
		Assertions.assertEquals(99L, returnValue);
	}

	@Test
	public void shouldIgnoreFileOnCalculatingStorage() {
		// Arrange
		BlobItemProperties itemProperties = Mockito.mock();

		Mockito.when(defaultIteratorBlobItem.getProperties()).thenReturn(itemProperties);

		Mockito.when(itemProperties.getContentLength()).thenReturn(99L);

		Mockito.when(defaultIteratorBlobItem.getName()).thenReturn("test");

		// Act
		long returnValue = userFilesService.getUserUsedStorage(fileOwner.getUsername(), "test");

		// Assert
		Assertions.assertEquals(0L, returnValue);
	}

	@Test
	public void shouldCreateFolder() {
		// Arrange
		String foldername = TestUtils.generateRandomFilename();

		BlobClient blobClient = Mockito.mock();

		Mockito.when(blobContainerClient.getBlobClient(foldername + "/.hidden"))
				.thenReturn(blobClient);

		Mockito.when(blobClient.exists()).thenReturn(false);

		// Act
		userFilesService.createFolder(fileOwner.getUsername(), foldername);

		// Assert
		Mockito.verify(blobClient).upload(Mockito.argThat((ArgumentMatcher<BinaryData>) json -> {
			return json.toString().equals(" ");
		}));
	}

	@Test
	public void shouldNotCreateAlreadyExistingFolder() {
		// Arrange
		String foldername = TestUtils.generateRandomFilename();

		BlobClient blobClient = Mockito.mock();

		Mockito.when(blobContainerClient.getBlobClient(foldername + "/.hidden"))
				.thenReturn(blobClient);

		Mockito.when(blobClient.exists()).thenReturn(true);

		// Act
		userFilesService.createFolder(fileOwner.getUsername(), foldername);

		// Assert
		Mockito.verify(blobClient, Mockito.never())
				.upload(Mockito.argThat((ArgumentMatcher<BinaryData>) json -> {
					return json.toString().equals(" ");
				}));
	}

	@Test
	public void shouldDeleteFolder() {
		// Arrange
		String foldername = TestUtils.generateRandomFilename();
		String blobClientUrl = "custom-url";

		BlobClient blobClient = Mockito.mock();

		Mockito.when(defaultIteratorBlobItem.getName()).thenReturn(foldername + "/blob");

		Mockito.when(blobContainerClient.getBlobClient(defaultIteratorBlobItem.getName()))
				.thenReturn(blobClient);

		Mockito.when(blobClient.getBlobUrl()).thenReturn(blobClientUrl);

		PagedIterable<Response<Void>> mockIterable = Mockito.mock();

		Mockito.when(blobBatchClient.deleteBlobs(Mockito.any(), Mockito.any()))
				.thenReturn(mockIterable);

		// Act
		userFilesService.deleteFolder(fileOwner.getUsername(), foldername);

		// Assert
		Mockito.verify(blobBatchClient).deleteBlobs(
				argThat(urls -> urls.size() == 1 && urls.get(0) == blobClientUrl),
				argThat(t -> true));
	}

	@Test
	public void shouldGetFilesSharedWithUser() {
		// Arrange
		User user = TestUtils.generateRandomUser();
		List<SharedFile> sharedFiles = new ArrayList<>();

		Mockito.when(fileShareDAO.getFilesSharedWithUser(user)).thenReturn(sharedFiles);

		// Act
		List<SharedFile> returnValue = userFilesService.getFilesSharedWithUser(user);

		// Assert
		Assertions.assertEquals(sharedFiles, returnValue);
	}

	@Test
	public void shouldGetFileShares() {
		// Arrange
		User user = TestUtils.generateRandomUser();
		List<SharedFile> sharedFiles = new ArrayList<>();

		Mockito.when(fileShareDAO.getFileShares(user, filename)).thenReturn(sharedFiles);

		// Act
		List<SharedFile> returnValue = userFilesService.getFileShares(user, filename);

		// Assert
		Assertions.assertEquals(sharedFiles, returnValue);
	}

	@Test
	public void shouldShareFileWith() throws BadRequestException {
		// Arrange
		String sharedWithUsername = TestUtils.generateRandomFilename();

		Mockito.when(blobClient.exists()).thenReturn(true);
		Mockito.when(fileShareDAO.isFileSharedWith(fileOwner.getUsername(), filename,
				sharedWithUsername)).thenReturn(false);

		// Act
		userFilesService.shareFileWith(fileOwner, filename, sharedWithUsername, false);

		// Assert
		Mockito.verify(fileShareDAO).shareFile(fileOwner.getUsername(), filename,
				sharedWithUsername, false);
	}

	@Test
	public void shouldNotShareFileWithBecauseFileDoesNotExist() {
		// Arrange
		String sharedWithUsername = TestUtils.generateRandomFilename();

		Mockito.when(blobClient.exists()).thenReturn(false);

		// Act & Assert
		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			userFilesService.shareFileWith(fileOwner, filename, sharedWithUsername, false);
		});
	}

	@Test
	public void shouldNotShareFileWithBecauseFileIsAlreadyShared() {
		// Arrange
		String sharedWithUsername = TestUtils.generateRandomFilename();

		Mockito.when(blobClient.exists()).thenReturn(true);
		Mockito.when(fileShareDAO.isFileSharedWith(fileOwner.getUsername(), filename,
				sharedWithUsername)).thenReturn(true);

		// Act & Assert
		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			userFilesService.shareFileWith(fileOwner, filename, sharedWithUsername, false);
		});
	}

	@Test
	public void shouldDeleteFileShare() throws BadRequestException {
		// Arrange
		String sharedWithUsername = TestUtils.generateRandomFilename();

		Mockito.when(fileShareDAO.isFileSharedWith(fileOwner.getUsername(), filename,
				sharedWithUsername)).thenReturn(true);

		// Act
		userFilesService.deleteShare(fileOwner, filename, sharedWithUsername);

		// Assert
		Mockito.verify(fileShareDAO).deleteFileShare(fileOwner.getUsername(), filename,
				sharedWithUsername);
	}

	@Test
	public void shouldNotDeleteShareBecauseFileIsNotShared() {
		// Arrange
		String sharedWithUsername = TestUtils.generateRandomFilename();

		Mockito.when(fileShareDAO.isFileSharedWith(fileOwner.getUsername(), filename,
				sharedWithUsername)).thenReturn(false);

		// Act & Assert
		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			userFilesService.deleteShare(fileOwner, filename, sharedWithUsername);
		});
	}

	@Test
	public void shouldUpdateSharedFileAccess() throws BadRequestException {
		// Arrange
		String sharedWithUsername = TestUtils.generateRandomFilename();
		UpdateSharedFileAccessRequest request = new UpdateSharedFileAccessRequest(false);

		Mockito.when(fileShareDAO.isFileSharedWith(fileOwner.getUsername(), filename,
				sharedWithUsername)).thenReturn(true);

		// Act
		userFilesService.updateSharedFileAccess(fileOwner, filename, sharedWithUsername, request);

		// Assert
		Mockito.verify(fileShareDAO).updateSharedFileAccess(fileOwner.getUsername(), filename,
				sharedWithUsername, request);
	}
}
