package com.brainy.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brainy.model.Response;
import com.brainy.model.ResponseWithoutData;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.exception.FileDoesNotExistException;
import com.brainy.service.UserFilesService;

@RestController
@RequestMapping("api")
public class UserFilesController {

	private UserFilesService userFilesService;

	public UserFilesController(UserFilesService userFilesService) {
		this.userFilesService = userFilesService;
	}

	@GetMapping("file")
	public Response<String> getFileContent(@RequestAttribute User user,
			@RequestParam String filename) throws FileDoesNotExistException {
		return new Response<String>(userFilesService.getFileContent(user.getUsername(), filename));
	}

	@GetMapping("files")
	public Response<List<String>> getUserFiles(@RequestAttribute User user) {
		return new Response<>(userFilesService.getUserFiles(user.getUsername()));
	}

	@PutMapping("file")
	public Response<String> updateJsonFile(@RequestAttribute User user,
			@RequestParam String filename, @RequestBody String body) throws BadRequestException {
		createOrUpdateJsonFile(user, filename, body);
		return new Response<>("the file has been updated");
	}

	/**
	 * @param body must be JSON
	 */
	@PostMapping("file")
	public Response<String> createJsonFile(@RequestAttribute User user,
			@RequestParam String filename, @RequestBody String body) throws BadRequestException {
		createOrUpdateJsonFile(user, filename, body);
		return new Response<>("the file has been created");
	}

	private void createOrUpdateJsonFile(User user, String filename, String body)
			throws BadRequestException {

		if (filename.isBlank())
			throw new BadRequestException("the filename must be at least one character long");

		boolean canUserCreateTheFile = userFilesService
				.canUserCreateFileWithSize(user.getUsername(), filename, body.length());

		if (!canUserCreateTheFile)
			throw new BadRequestException("there isn't enough space");

		userFilesService.createOrUpdateJsonFile(user.getUsername(), filename, body);
	}

	@DeleteMapping("file")
	public ResponseWithoutData deleteFile(@RequestAttribute User user,
			@RequestParam String filename) throws FileDoesNotExistException {

		userFilesService.deleteFile(user.getUsername(), filename);
		return new ResponseWithoutData();
	}

	/**
	 * @return how much storage the user has used in bytes
	 */
	@GetMapping("used-storage")
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
			@RequestParam String foldername) throws BadRequestException, FileDoesNotExistException {

		if (foldername.isBlank())
			throw new BadRequestException("the foldername must be at least one character long");

		userFilesService.deleteFolder(user.getUsername(), foldername);
		return new Response<String>("folder deleted");
	}
}
