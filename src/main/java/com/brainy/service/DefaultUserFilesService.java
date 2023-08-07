package com.brainy.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.batch.BlobBatchClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.DeleteSnapshotsOptionType;
import com.brainy.model.exception.BadRequestException;
import com.brainy.util.JsonUtil;

/**
 * Contains all operations that has to do with user files, it uses Microsoft
 * Azure.
 */
@Service
public class DefaultUserFilesService implements UserFilesService {

    private BlobBatchClient blobBatchClient;
    
    private BlobServiceClient blobServiceClient;

    @Value("${max-size-per-user}")
    private long maxStoragePerUser;

    @Value("${max-file-size}")
    private long maxFileSize;

    @Autowired
    public DefaultUserFilesService(BlobServiceClient blobServiceClient,
            BlobBatchClient blobBatchClient) {

        this.blobServiceClient = blobServiceClient;
        this.blobBatchClient = blobBatchClient;
    }

    public DefaultUserFilesService(BlobServiceClient blobServiceClient,
            BlobBatchClient blobBatchClient, long maxStoragePerUser,
            long maxFileSize) {

        this.blobServiceClient = blobServiceClient;
        this.blobBatchClient = blobBatchClient;
        this.maxStoragePerUser = maxStoragePerUser;
        this.maxFileSize = maxFileSize;
    }

    @Override
    public List<String> getUserFiles(String username) {
        BlobContainerClient containerClient = getUserBlobContainerClient(username);

        List<String> userFiles = new ArrayList<>();

        for (BlobItem blobItem : containerClient.listBlobs()) {
            userFiles.add(blobItem.getName());
        }

        return userFiles;
    }

    @Override
    public String getFileContent(String username, String filename) {
        BlobContainerClient containerClient = getUserBlobContainerClient(username);

        BlobClient blobClient = containerClient.getBlobClient(filename);

        BinaryData binaryData = blobClient.downloadContent();

        return binaryData.toString();
    }
    
    @Override
    public void createOrUpdateJsonFile(String username, String filename, 
            String content) throws BadRequestException {

        String compressedJson = getCompressedJson(content);

        BlobContainerClient containerClient = getUserBlobContainerClient(username);

        BlobClient blobClient = containerClient.getBlobClient(filename);

        blobClient.deleteIfExists();

        blobClient.upload(BinaryData.fromBytes(compressedJson.getBytes()));
    }

    private String getCompressedJson(String content)
            throws BadRequestException {

        String compressedJson = JsonUtil.compressJson(content);
        
        if (compressedJson == null)
            throw new BadRequestException("please provide valid JSON content!");
        
        return compressedJson;
    }

    @Override
    public void deleteFile(String username, String filename) {
        BlobContainerClient containerClient = getUserBlobContainerClient(username);

        BlobClient blobClient = containerClient.getBlobClient(filename);

        blobClient.deleteIfExists();
    }

    @Override
    public boolean canUserCreateFileWithSize(
            String username, String filename, long fileSize) {

        if (fileSize > maxFileSize)
            return false;

        long userFilesSize = getUserUsedStorage(username, filename);
        long filesSizeWithNewFile = userFilesSize + fileSize;

        return filesSizeWithNewFile <= maxStoragePerUser;
    }

    @Override
    public long getUserUsedStorage(String username, String ...filesToIgnore) {
        BlobContainerClient containerClient = getUserBlobContainerClient(username);

        long totalSize = 0;

        List<String> filesToIgnoreAsList = Arrays.asList(filesToIgnore);

        for (BlobItem blobItem : containerClient.listBlobs()) {

            if (!filesToIgnoreAsList.contains(blobItem.getName()))
                totalSize += blobItem.getProperties().getContentLength();
        }

        return totalSize;
    }

    /**
     * Folders doesn't exist in Azure, so instead we name files using folder
     * names as prefix. When creating a new folder, we just make a file inside
     * the folder, the file is called ".hidden" and should not be displayed
     * in front-end.
     */
    @Override
    public void createFolder(String username, String foldername) {
        BlobContainerClient containerClient = getUserBlobContainerClient(username);

        BlobClient blobClient = containerClient
                .getBlobClient(foldername + "/.hidden");

        if (blobClient.exists())
            return;

        blobClient.upload(BinaryData.fromBytes(" ".getBytes()));
    }

    @Override
    public void deleteFolder(String username, String foldername) {
        List<String> deleteUrls = new ArrayList<>();

        BlobContainerClient containerClient = getUserBlobContainerClient(username);

        for (BlobItem blobItem : containerClient.listBlobs()) {
            if (blobItem.getName().startsWith(foldername + "/")) {
                deleteUrls.add(getBlobItemUrl(blobItem, containerClient));
            }
        }

        blobBatchClient
                .deleteBlobs(deleteUrls, DeleteSnapshotsOptionType.INCLUDE)
                .forEach(response -> {
                    // The forEach is necessary for the delete batch
                });
    }

    private String getBlobItemUrl(BlobItem blobItem,
            BlobContainerClient containerClient) {

        BlobClient blobClient = containerClient.getBlobClient(blobItem.getName());

        // Replacing %2F with /, because Azure SDK replaces / with %2F
        return blobClient.getBlobUrl().replaceAll("%2F", "/");
    }

    private BlobContainerClient getUserBlobContainerClient(String username) {
        return blobServiceClient.createBlobContainerIfNotExists(username);
    }
}
