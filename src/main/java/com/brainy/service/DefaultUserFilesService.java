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
import com.brainy.dao.FileShareDao;
import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UpdateSharedFileAccessRequest;
import com.brainy.util.JsonUtil;

/**
 * Contains all operations that has to do with user files, it uses Microsoft
 * Azure.
 */
@Service
public class DefaultUserFilesService implements UserFilesService {

    private BlobBatchClient blobBatchClient;
    
    private BlobServiceClient blobServiceClient;

    private FileShareDao fileShareDAO;

    @Value("${max-size-per-user}")
    private long maxStoragePerUser;

    @Value("${max-file-size}")
    private long maxFileSize;

    @Autowired
    public DefaultUserFilesService(BlobServiceClient blobServiceClient,
            BlobBatchClient blobBatchClient, FileShareDao fileShareDAO) {

        this.blobServiceClient = blobServiceClient;
        this.blobBatchClient = blobBatchClient;
        this.fileShareDAO = fileShareDAO;
    }

    public DefaultUserFilesService(BlobServiceClient blobServiceClient,
            BlobBatchClient blobBatchClient, FileShareDao fileShareDAO,
            long maxStoragePerUser, long maxFileSize) {

        this.blobServiceClient = blobServiceClient;
        this.blobBatchClient = blobBatchClient;
        this.fileShareDAO = fileShareDAO;
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
        if (!foldername.endsWith("/"))
            foldername = foldername + "/";

        List<String> deleteUrls = new ArrayList<>();

        BlobContainerClient containerClient = getUserBlobContainerClient(username);

        for (BlobItem blobItem : containerClient.listBlobs()) {
            if (blobItem.getName().startsWith(foldername)) {
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

    @Override
    public List<SharedFile> getFilesSharedWithUser(User user) {
        return fileShareDAO.getFilesSharedWithUser(user);
    }

    @Override
    public List<SharedFile> getFileShares(User user, String filename) {
        return fileShareDAO.getFileShares(user, filename);
    }

    @Override
    public void shareFileWith(User fileOwner, String filename,
            String sharedWithUsername, boolean canEdit)
            throws BadRequestException {

        BlobContainerClient fileOwnerBlobContainerClient =
                getUserBlobContainerClient(fileOwner.getUsername());

        BlobClient fileBlobClient = fileOwnerBlobContainerClient
                .getBlobClient(filename);

        if (!fileBlobClient.exists())
            throw new BadRequestException("couldn't find the file " + filename);

        boolean isFileAlreadyShard = fileShareDAO.isFileSharedWith(
            fileOwner.getUsername(), filename, sharedWithUsername);

        if (isFileAlreadyShard)
            throw new BadRequestException("the file is already shared!");
        
        fileShareDAO.shareFile(fileOwner.getUsername(), filename,
                sharedWithUsername, canEdit);
    }

    private BlobContainerClient getUserBlobContainerClient(String username) {
        return blobServiceClient.createBlobContainerIfNotExists(username);
    }

    @Override
    public void deleteShare(User fileOwner, String filename,
            String sharedWithUsername) throws BadRequestException {

        String fileOwnerUsername = fileOwner.getUsername();

        validateThatAFileIsShared(filename, sharedWithUsername, fileOwnerUsername);

        fileShareDAO.deleteFileShare(fileOwnerUsername, filename, sharedWithUsername);
    }

    @Override
    public void updateSharedFileAccess(User fileOwner, String filename,
            String sharedWithUsername, UpdateSharedFileAccessRequest request)
            throws BadRequestException {

        String fileOwnerUsername = fileOwner.getUsername();

        validateThatAFileIsShared(filename, sharedWithUsername, fileOwnerUsername);
        
        fileShareDAO.updateSharedFileAccess(fileOwnerUsername, filename,
                sharedWithUsername, request);
    }

    private void validateThatAFileIsShared(String filename, 
            String sharedWithUsername, String fileOwnerUsername)
            throws BadRequestException {

        if (!fileShareDAO.isFileSharedWith(fileOwnerUsername, filename,
                sharedWithUsername))
            throw new BadRequestException("you didn't share the file with the user");
    }
}
