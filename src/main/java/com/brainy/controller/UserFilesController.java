package com.brainy.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brainy.model.Response;
import com.brainy.model.ResponseWithoutData;
import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.exception.FileDoesNotExistException;
import com.brainy.model.request.UpdateSharedFileAccessRequest;
import com.brainy.service.UserFilesService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/files")
public class UserFilesController {

	private UserFilesService userFilesService;

	public UserFilesController(UserFilesService userFilesService) {
		this.userFilesService = userFilesService;
	}

	@GetMapping
	public Response<Object> getFileContentOrUserFiles(@RequestAttribute User user,
			@RequestParam(required = false) String filename) throws FileDoesNotExistException {

		Object body = null;

		if (filename != null) {
			body = userFilesService.getFileContent(user.getUsername(), filename);
		} else {
			body = userFilesService.getUserFiles(user.getUsername());
		}

		return new Response<>(body);
	}

	/**
	 * @param body must be JSON
	 */
	@PostMapping
	public Response<String> createOrUpdateJsonFile(@RequestAttribute User user,
			@RequestParam String filename, @RequestBody String body) throws BadRequestException {

		if (filename.isBlank())
			throw new BadRequestException("the filename must be at least one character long");

		boolean canUserCreateTheFile = userFilesService
				.canUserCreateFileWithSize(user.getUsername(), filename, body.length());

		if (!canUserCreateTheFile)
			throw new BadRequestException("there isn't enough space");

		userFilesService.createOrUpdateJsonFile(user.getUsername(), filename, body);
		return new Response<>("the file has been created");
	}

	@DeleteMapping
	public ResponseWithoutData deleteFile(@RequestAttribute User user,
			@RequestParam String filename) throws FileDoesNotExistException {

		userFilesService.deleteFile(user.getUsername(), filename);
		return new ResponseWithoutData();
	}

	@GetMapping("size")
	public Response<Long> getUserUsedStorage(@RequestAttribute User user) {
		long size = userFilesService.getUserUsedStorage(user.getUsername());
		return new Response<>(size);
	}

	@PostMapping("folder")
	public Response<String> createFolder(@RequestAttribute User user,
			@RequestParam String foldername) throws BadRequestException {

		if (foldername.isBlank())
			throw new BadRequestException("the foldername must be at least one character long");

		userFilesService.createFolder(user.getUsername(), foldername);

		return new Response<String>("folder created");
	}

	@DeleteMapping("folder")
	public Response<String> deleteFolder(@RequestAttribute User user,
			@RequestParam String foldername) throws BadRequestException {

		if (foldername.isBlank())
			throw new BadRequestException("the foldername must be at least one character long");

		userFilesService.deleteFolder(user.getUsername(), foldername);

		return new Response<String>("folder deleted");
	}

	@GetMapping("shared-with-me")
	public Response<List<SharedFile>> getFilesSharedWithUser(@RequestAttribute User user) {
		List<SharedFile> sharedFiles = userFilesService.getFilesSharedWithUser(user);

		return new Response<>(sharedFiles);
	}

	@GetMapping("share")
	public Response<List<SharedFile>> getFileShares(@RequestAttribute User user,
			@RequestParam String filename) {

		List<SharedFile> sharedFiles = userFilesService.getFileShares(user, filename);
		return new Response<>(sharedFiles);
	}

	@PostMapping("share")
	public Response<String> shareFileWith(@RequestAttribute(name = "user") User fileOwner,
			@RequestParam String filename,
			@RequestParam(name = "shared-with") String sharedWithUsername,
			@RequestParam(defaultValue = "false", name = "can-edit") boolean canEdit)
			throws BadRequestException {

		userFilesService.shareFileWith(fileOwner, filename, sharedWithUsername, canEdit);
		return new Response<String>("file shared successfully");
	}

	@DeleteMapping("share")
	public Response<String> deleteShare(@RequestAttribute(name = "user") User fileOwner,
			@RequestParam String filename,
			@RequestParam(name = "shared-with") String sharedWithUsername)
			throws BadRequestException {

		userFilesService.deleteShare(fileOwner, filename, sharedWithUsername);

		return new Response<String>("removed the share successfully");
	}

	@PatchMapping("share")
	public Response<String> updateSharedFileAccess(@RequestAttribute(name = "user") User fileOwner,
			@RequestParam String filename,
			@RequestParam(name = "shared-with") String sharedWithUsername,
			@RequestBody @Valid UpdateSharedFileAccessRequest request) throws BadRequestException {

		userFilesService.updateSharedFileAccess(fileOwner, filename, sharedWithUsername, request);

		return new Response<>("the update has been applied");
	}
}
