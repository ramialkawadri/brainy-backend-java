package com.brainy.service;

import java.util.List;

import com.brainy.model.exception.BadRequestException;

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
     * when calculating the size!
     */
    long getUserUsedStorage(String username, String ...filesToIgnore);
}
