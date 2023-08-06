package com.brainy.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.brainy.model.exception.BadRequestException;
import com.brainy.utils.Validator;

// TODO: test
@Service
public class DefaultUserFilesService implements UserFilesService {

    private BlobServiceClient blobServiceClient;

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
    public void uploadJsonFile(String username, String filename, String content) 
            throws BadRequestException {

        validateThatContentIsJson(content);

        // TODO: compress JSON

        BlobContainerClient containerClient = getUserBlobContainerClient(username);

        BlobClient blobClient = containerClient.getBlobClient(filename);

        blobClient.deleteIfExists();

        blobClient.upload(BinaryData.fromBytes(content.getBytes()));
    }

    private void validateThatContentIsJson(String content)
            throws BadRequestException {

        if (!Validator.isValidJson(content))
            throw new BadRequestException("please provide valid JSON content!");
    }

    @Override
    public void deleteFile(String username, String filename) {
        BlobContainerClient containerClient = getUserBlobContainerClient(username);

        BlobClient blobClient = containerClient.getBlobClient(filename);

        blobClient.deleteIfExists();
    }

    private BlobContainerClient getUserBlobContainerClient(String username) {
        return blobServiceClient.createBlobContainerIfNotExists(username);
    }
}
