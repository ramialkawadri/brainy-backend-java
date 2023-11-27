package com.brainy.service;

import java.util.List;

import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UpdateSharedFileAccessRequest;

public interface UserFilesService {
    List<String> getUserFiles(String username);

    String getFileContent(String username, String filename);

    void createOrUpdateJsonFile(String username, String filename, String content)
            throws BadRequestException;

    void deleteFile(String username, String filename);

    /**
     * Returns if the user have enough size to create a file with the given size.
     * If the file already exists its previous size is not taken into account
     * when creating the file.
     */
    boolean canUserCreateFileWithSize(
            String username, String filename, long fileSize);

    /**
     * @param filesToIgnore is a set of file names that are not considered
     *                      when calculating the size!
     */
    long getUserUsedStorage(String username, String... filesToIgnore);

    void createFolder(String username, String foldername);

    void deleteFolder(String username, String foldername);

    List<SharedFile> getFilesSharedWithUser(User user);

    List<SharedFile> getFileShares(User user, String filename);

    void shareFileWith(User fileOwner, String filename, String sharedWithUsername,
            boolean canEdit) throws BadRequestException;

    void deleteShare(User fileOwner, String filename, String sharedWithUsername)
            throws BadRequestException;

    void updateSharedFileAccess(User fileOwner, String filename,
            String sharedWithUsername, UpdateSharedFileAccessRequest request)
            throws BadRequestException;
}
