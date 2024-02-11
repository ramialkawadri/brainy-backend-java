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
import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UpdateSharedFileAccessRequest;
import com.brainy.service.UserFilesService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api")
public class FileSharingController {

	private UserFilesService userFilesService;

	public FileSharingController(UserFilesService userFilesService) {
		this.userFilesService = userFilesService;
	}

	@GetMapping("shared-with-me")
	public Response<List<SharedFile>> getFilesSharedWithUser(@RequestAttribute User user) {
		List<SharedFile> sharedFiles = userFilesService.getFilesSharedWithUser(user);

		return new Response<>(sharedFiles);
	}

	@GetMapping("file-shares")
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

	@GetMapping("share")
	public Response<String> getSharedFileContent(
			@RequestAttribute(name = "user") User sharedWithUser, @RequestParam String filename,
			@RequestParam(name = "file-owner") String fileOwnerUsername)
			throws BadRequestException {

		String fileContent = userFilesService.getSharedFileContent(fileOwnerUsername, filename,
				sharedWithUser.getUsername());

		return new Response<String>(fileContent);
	}
}
