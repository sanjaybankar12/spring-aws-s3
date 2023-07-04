package com.spring.service;

import org.springframework.web.multipart.MultipartFile;

public interface AmazonS3Service {

	String uploadFile(MultipartFile file);

	String deleteFile(String fileName);

	byte[] downloadFile(String fileName);

}
