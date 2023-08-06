package com.brainy.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.brainy.model.exception.BadRequestException;
import com.brainy.util.JsonUtil;

/**
 * Contains all operations that has to do with user files, it uses Microsoft
 * Azure.
 */
@Service
public class DefaultUserFilesService implements UserFilesService {

    private BlobServiceClient blobServiceClient;

    @Value("${max-size-per-user}")
    private Long maxStoragePerUser;

    @Value("${max-file-size}")
    private long maxFileSize;

    public DefaultUserFilesService(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
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

    private BlobContainerClient getUserBlobContainerClient(String username) {
        return blobServiceClient.createBlobContainerIfNotExists(username);
    }
}
