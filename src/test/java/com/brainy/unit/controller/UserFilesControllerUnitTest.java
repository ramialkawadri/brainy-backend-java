package com.brainy.unit.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.brainy.TestUtils;
import com.brainy.controller.UserFilesController;
import com.brainy.model.Response;
import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UpdateSharedFileAccessRequest;
import com.brainy.service.UserFilesService;

public class UserFilesControllerUnitTest {

    private UserFilesService userFilesService;
    private UserFilesController userFilesController;

    public UserFilesControllerUnitTest() {
        userFilesService = Mockito.mock();
        userFilesController = new UserFilesController(userFilesService);
    }

    @Test
    public void shouldReturnUserFiles() {
        List<String> userFiles = new ArrayList<>();
        userFiles.add(TestUtils.generateRandomFilename());
        userFiles.add(TestUtils.generateRandomFilename());

        User user = TestUtils.generateRandomUser();

        Mockito.when(userFilesService.getUserFiles(user.getUsername()))
                .thenReturn(userFiles);

        Response<Object> returnValue = userFilesController
                .getFileContentOrUserFiles(user, null);

        Mockito.verify(userFilesService).getUserFiles(user.getUsername());

        Assertions.assertEquals(userFiles, returnValue.getData());
    }

    @Test
    public void shouldReturnFileContent() {
        String fileContent = TestUtils.generateRandomFileContent();
        String filename = TestUtils.generateRandomFilename();
        User user = TestUtils.generateRandomUser();

        Mockito.when(userFilesService.getFileContent(user.getUsername(), filename))
                .thenReturn(fileContent);

        Response<Object> returnValue = userFilesController
                .getFileContentOrUserFiles(user, filename);

        Mockito.verify(userFilesService).getFileContent(user.getUsername(), filename);

        Assertions.assertEquals(fileContent, returnValue.getData());
    }

    @Test
    public void shouldCreateOrUpdateFile() throws BadRequestException {
        String fileContent = TestUtils.generateRandomFileContent();
        String filename = TestUtils.generateRandomFilename();
        User user = TestUtils.generateRandomUser();

        Mockito.when(userFilesService.canUserCreateFileWithSize(
                user.getUsername(), filename, fileContent.length()))
                .thenReturn(true);

        userFilesController.createOrUpdateJsonFile(user, filename, fileContent);

        Mockito.verify(userFilesService).createOrUpdateJsonFile(
                user.getUsername(), filename, fileContent);
    }

    @Test
    public void shouldNotCreateOrUpdateFile() {
        String fileContent = TestUtils.generateRandomFileContent();
        String filename = TestUtils.generateRandomFilename();
        User user = TestUtils.generateRandomUser();

        Mockito.when(userFilesService.canUserCreateFileWithSize(
                user.getUsername(), filename, fileContent.length()))
                .thenReturn(false);

        Assertions.assertThrowsExactly(BadRequestException.class, () -> {
            userFilesController
                    .createOrUpdateJsonFile(user, filename, fileContent);
        });
    }

    @Test
    public void shouldDeleteFile() {
        User user = TestUtils.generateRandomUser();
        String filename = TestUtils.generateRandomFilename();

        userFilesController.deleteFile(user, filename);

        Mockito.verify(userFilesService).deleteFile(user.getUsername(), filename);
    }

    @Test
    public void shouldGetUserUsedStorage() {
        User user = TestUtils.generateRandomUser();
        long usedStorage = 100000L;

        Mockito.when(userFilesService.getUserUsedStorage(user.getUsername()))
                .thenReturn(usedStorage);

        long returnValue = userFilesController.getUserUsedStorage(user).getData();

        Mockito.verify(userFilesService).getUserUsedStorage(user.getUsername());
        Assertions.assertEquals(returnValue, usedStorage);
    }

    @Test
    public void shouldCreateFolder() throws BadRequestException {
        User user = TestUtils.generateRandomUser();
        String foldername = TestUtils.generateRandomFilename();

        userFilesController.createFolder(user, foldername);

        Mockito.verify(userFilesService).createFolder(user.getUsername(), foldername);
    }

    @Test
    public void shouldDeleteFolder() {
        User user = TestUtils.generateRandomUser();
        String foldername = TestUtils.generateRandomFilename();

        userFilesController.deleteFile(user, foldername);

        Mockito.verify(userFilesService).deleteFile(user.getUsername(), foldername);
    }

    @Test
    public void shouldGetFilesSharedWithUser() {
        User user = TestUtils.generateRandomUser();
        List<SharedFile> list = new ArrayList<>();

        Mockito.when(userFilesService.getFilesSharedWithUser(user))
                .thenReturn(list);

        Response<List<SharedFile>> response = userFilesController.getFilesSharedWithUser(user);

        Mockito.verify(userFilesService).getFilesSharedWithUser(user);

        Assertions.assertEquals(list, response.getData());
    }

    @Test
    public void shouldGetFileShares() {
        User user = TestUtils.generateRandomUser();
        List<SharedFile> list = new ArrayList<>();
        String filename = TestUtils.generateRandomFilename();

        Mockito.when(userFilesService.getFileShares(user, filename))
                .thenReturn(list);

        Response<List<SharedFile>> response = userFilesController.getFileShares(user, filename);

        Mockito.verify(userFilesService).getFileShares(user, filename);

        Assertions.assertEquals(list, response.getData());
    }

    @Test
    public void shouldShareFile() throws BadRequestException {
        User user = TestUtils.generateRandomUser();
        String filename = TestUtils.generateRandomFilename();
        String sharedWithUsername = TestUtils.generateUniqueUsername();

        userFilesController.shareFileWith(user, filename, sharedWithUsername, false);

        Mockito.verify(userFilesService)
                .shareFileWith(user, filename, sharedWithUsername, false);
    }

    @Test
    public void shouldDeleteFileShare() throws BadRequestException {
        User user = TestUtils.generateRandomUser();
        String filename = TestUtils.generateRandomFilename();
        String sharedWithUsername = TestUtils.generateUniqueUsername();

        userFilesController.deleteShare(user, filename, sharedWithUsername);

        Mockito.verify(userFilesService).deleteShare(user, filename, sharedWithUsername);
    }

    @Test
    public void shouldUpdateSharedFileAccess() throws BadRequestException {
        User user = TestUtils.generateRandomUser();
        String filename = TestUtils.generateRandomFilename();
        String sharedWithUsername = TestUtils.generateUniqueUsername();
        UpdateSharedFileAccessRequest request = new UpdateSharedFileAccessRequest(true);

        userFilesController.updateSharedFileAccess(user, filename,
                sharedWithUsername, request);

        Mockito.verify(userFilesService).updateSharedFileAccess(user, filename,
                sharedWithUsername, request);
    }
}
