package com.brainy.dao;

import java.util.List;

import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UpdateSharedFileAccessRequest;

public interface FileShareDao {

    List<SharedFile> getFilesSharedWithUser(User user);

    List<SharedFile> getFileShares(User user, String filename);

    boolean isFileSharedWith(String fileOwnerUsername, String filename,
            String sharedWithUsername);

    void shareFile(String fileOwnerUsername, String filename,
            String sharedWithUsername, boolean canEdit)
            throws BadRequestException;

    void deleteFileShare(String fileOwnerUsername, String filename,
            String sharedWithUsername);

    void updateSharedFileAccess(String fileOwnerUsername, String filename,
            String sharedWithUsername, UpdateSharedFileAccessRequest request);
}
