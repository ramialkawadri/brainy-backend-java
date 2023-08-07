package com.brainy.unit.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.service.DefaultUserFilesService;
import com.brainy.service.UserFilesService;

public class DefaultUserFilesServiceUnitTest {
    
    private BlobServiceClient blobServiceClient;
    private BlobContainerClient blobContainerClient;
    private BlobItem defaultIteratorBlobItem;
    private BlobClient blobClient;
    private long maxStoragePerUser = 500;
    private long maxFileSize = 100;
    private BlobBatchClient blobBatchClient;
    private UserFilesService userFilesService;

    @BeforeEach
    public void setup() {
        blobServiceClient = Mockito.mock();
        blobContainerClient = Mockito.mock();
        blobBatchClient = Mockito.mock();
        blobClient = Mockito.mock();

        Mockito.when(blobContainerClient.getBlobClient(Mockito.any()))
                .thenReturn(blobClient);
        
        Mockito.when(blobServiceClient
                .createBlobContainerIfNotExists(Mockito.anyString()))
                .thenReturn(blobContainerClient);

        setupIterator();

        userFilesService = new DefaultUserFilesService(blobServiceClient,
                blobBatchClient, maxStoragePerUser, maxFileSize);
    }

    private void setupIterator() {
        PagedIterable<BlobItem> pagedIterable = Mockito.mock();
        defaultIteratorBlobItem = Mockito.mock();

        Mockito.when(blobContainerClient.listBlobs()).thenReturn(pagedIterable);
        Mockito.when(pagedIterable.iterator()).thenAnswer(
            a -> List.of(defaultIteratorBlobItem).iterator()
        );
    }

    @Test
    public void shouldGetUserFiles() {
        User tesUser = TestUtils.generateRandomUser();

        Mockito.when(defaultIteratorBlobItem.getName()).thenReturn("test");

        List<String> userFiles = 
                userFilesService.getUserFiles(tesUser.getUsername());

        Mockito.verify(blobContainerClient).listBlobs();
        
        Assertions.assertEquals("test", userFiles.get(0));
    }

    @Test
    public void shouldGetFileContent() {
        String fileContent = "RANDOM_STRING";
        String filename = "filename";
        
        User user = TestUtils.generateRandomUser();

        Mockito.when(blobClient.downloadContent())
                .thenReturn(BinaryData.fromBytes(fileContent.getBytes()));

        String returnValue = 
                userFilesService.getFileContent(user.getUsername(), filename);
        
        Assertions.assertEquals(fileContent, returnValue);
    }

    @Test
    public void shouldCreateOrUpdateJsonFile() throws BadRequestException {
        String jsonContent = "{ \"isJson\": true }";

        User user = TestUtils.generateRandomUser();
        String filename = "filename";

        userFilesService.createOrUpdateJsonFile(
                user.getUsername(), filename, jsonContent);
        
        Mockito.verify(blobClient).deleteIfExists();
        
        Mockito.verify(blobClient).upload(Mockito.any(BinaryData.class));
    }

    @Test
    public void shouldNotAcceptInvalidJson() {
        String invalidJson = "aa { \"isJson\": true }";

        User user = TestUtils.generateRandomUser();
        String filename = "filename";

        Assertions.assertThrowsExactly(BadRequestException.class, () -> {
            userFilesService.createOrUpdateJsonFile(user.getUsername(),
                    filename, invalidJson);
        });
    }

    @Test
    public void shouldDeleteFile() {
        User user = TestUtils.generateRandomUser();
        String filename = "filename";

        userFilesService.deleteFile(user.getUsername(), filename);

        Mockito.verify(blobClient).deleteIfExists();
    }

    @Test
    public void shouldReturnTrueOnCanUserCreateFileWithSize() {
        BlobItemProperties itemProperties = Mockito.mock();

        Mockito.when(defaultIteratorBlobItem.getProperties())
                .thenReturn(itemProperties);

        Mockito.when(itemProperties.getContentLength())
                .thenReturn(maxFileSize);

        boolean returnValue = userFilesService
                .canUserCreateFileWithSize("user", "filename", 1);

        Assertions.assertTrue(returnValue);
    }

    @Test
    public void shouldReturnFalseOnCanUserCreateFileWithSize() {
        BlobItemProperties itemProperties = Mockito.mock();

        Mockito.when(defaultIteratorBlobItem.getProperties())
                .thenReturn(itemProperties);

        Mockito.when(itemProperties.getContentLength())
                .thenReturn(maxFileSize + 1);

        boolean returnValue = userFilesService
                .canUserCreateFileWithSize("user", "filename", 1);

        Assertions.assertTrue(returnValue);
    }

    @Test
    public void shouldGetUserUsedStorage() {
        BlobItemProperties itemProperties = Mockito.mock();

        Mockito.when(defaultIteratorBlobItem.getProperties())
                .thenReturn(itemProperties);

        Mockito.when(itemProperties.getContentLength())
                .thenReturn(99L);

        long returnValue = userFilesService.getUserUsedStorage("user");

        Assertions.assertEquals(99L, returnValue);
    }

    @Test
    public void shouldIgnoreFileOnCalculatingStorage() {
        BlobItemProperties itemProperties = Mockito.mock();

        Mockito.when(defaultIteratorBlobItem.getProperties())
                .thenReturn(itemProperties);

        Mockito.when(itemProperties.getContentLength())
                .thenReturn(99L);

        Mockito.when(defaultIteratorBlobItem.getName())
                .thenReturn("test");

        long returnValue = userFilesService.getUserUsedStorage("user", "test");

        Assertions.assertEquals(0L, returnValue);
    }

    @Test
    public void shouldCreateFolder() {
        String username = "username";
        String foldername = "foldername";

        BlobClient blobClient = Mockito.mock();

        Mockito.when(blobContainerClient.getBlobClient(foldername + "/.hidden"))
                .thenReturn(blobClient);

        Mockito.when(blobClient.exists()).thenReturn(false);

        userFilesService.createFolder(username, foldername);

        Mockito.verify(blobClient).upload(Mockito.any(BinaryData.class));
    }

    @Test
    public void shouldDeleteFolder() {
        String username = "username";
        String foldername = "foldername";
        String blobClientUrl = "custom-url";

        BlobClient blobClient = Mockito.mock();

        Mockito.when(blobContainerClient.getBlobClient(Mockito.any()))
                .thenReturn(blobClient);

        Mockito.when(blobClient.getBlobUrl()).thenReturn(blobClientUrl);

        Mockito.when(defaultIteratorBlobItem.getName())
                .thenReturn(foldername + "/blob");

        PagedIterable<Response<Void>> mockIterable = Mockito.mock();

        Mockito.doAnswer((invocation) -> {
            List<String> urls = invocation.getArgument(0);

            Assertions.assertEquals(1, urls.size());

            return mockIterable;
        }).when(blobBatchClient).deleteBlobs(Mockito.any(), Mockito.any());

        userFilesService.deleteFolder(username, foldername);
    }
}
