package com.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.service.AmazonS3Service;

@RestController
@RequestMapping("/file")
public class StorageController {

	@Autowired
	private AmazonS3Service amazonS3Service;
	
	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file) {
		return new ResponseEntity<>(this.amazonS3Service.uploadFile(file), HttpStatus.CREATED);
	}
	
	@GetMapping("/download/{fileName}")
	public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
		byte[] data = this.amazonS3Service.downloadFile(fileName);
		ByteArrayResource resource = new ByteArrayResource(data);
		
		return ResponseEntity
				.ok()
				.contentLength(data.length)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header("content-disposition", "attachment; filename=\"" + fileName + "\"")
				.body(resource);
	}
	
	@DeleteMapping("/{fileName}")
	@ResponseStatus(value = HttpStatus.OK)
	public String deleteFile(@PathVariable String fileName) {
		return this.amazonS3Service.deleteFile(fileName);
	}
}
