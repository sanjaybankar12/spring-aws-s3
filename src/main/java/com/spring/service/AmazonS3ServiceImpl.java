package com.spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class AmazonS3ServiceImpl implements AmazonS3Service {
	
	Logger log = LoggerFactory.getLogger(AmazonS3ServiceImpl.class);

	@Value("${cloud.aws.s3.bucket-name}")
	private String bucketName;
	
	@Autowired
	private com.amazonaws.services.s3.AmazonS3 amazonS3;
	
	@Autowired
	private TransferManager transferManager;
	
	@Override
	public void uploadFileUsingMutlipart(MultipartFile file) {
		File convertedFile = convertMultipartFile(file);
		String key = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		
		PutObjectRequest putRequest = new PutObjectRequest(bucketName, key, convertedFile);
		Upload upload= transferManager.upload(putRequest);
		try {
			upload.waitForCompletion();
		} catch (AmazonClientException |InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public String uploadFile(MultipartFile file) {
		File convertedFile = convertMultipartFile(file);
		String key = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		amazonS3.putObject(new PutObjectRequest(bucketName, key, convertedFile));
		convertedFile.delete();
		return "File uploded successfully!!";
	}
	
	private File convertMultipartFile(MultipartFile file) {
		File convertedFile = new File(file.getOriginalFilename());
		try(FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(file.getBytes());
		} catch(Exception e) {
			log.error("Error occur while converting file");
		}
		return convertedFile;
	}
	
	@Override
	public byte[] downloadFile(String fileName) {
		byte[] content = {};
		
		S3Object s3Object = amazonS3.getObject(bucketName, fileName);
		S3ObjectInputStream inputStream = s3Object.getObjectContent();
		try {
			content = IOUtils.toByteArray(inputStream);
		} catch (IOException e) {
			log.error("Error occur while downloading file");
		}
		return content;
	}
	
	@Override
	public String deleteFile(String fileName) {
		amazonS3.deleteObject(bucketName, fileName);
		return fileName +" removed!";
	}
	
}
