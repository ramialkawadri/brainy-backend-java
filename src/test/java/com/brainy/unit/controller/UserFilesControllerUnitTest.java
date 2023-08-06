package com.brainy.unit.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.brainy.TestUtils;
import com.brainy.controller.UserFilesController;
import com.brainy.model.Response;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
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
        userFiles.add("file 1");
        userFiles.add("file 2");

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
        String fileContent = "RANDOM_STRING";
        String filename = "filename";
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
        String fileContent = "RANDOM_STRING";
        String filename = "filename";
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
        String fileContent = "RANDOM_STRING";
        String filename = "filename";
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
        String filename = "filename";

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
}
