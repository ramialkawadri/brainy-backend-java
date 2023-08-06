package com.brainy.service;

import java.util.List;

import com.brainy.model.exception.BadRequestException;

public interface UserFilesService {
    List<String> getUserFiles(String username); 

    String getFileContent(String username, String filename);

    void uploadJsonFile(String username, String filename, String content)
            throws BadRequestException;

    void deleteFile(String username, String filename);
}
