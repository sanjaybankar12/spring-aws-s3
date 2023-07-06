package com.spring.config;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

@Configuration
public class AmazonS3Config {
	
	@Value("${cloud.aws.region.static}")
	private String region;
	
	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;
	
	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Bean
	public AmazonS3 amazonS3() {
		return AmazonS3ClientBuilder.standard().withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
				.build();
	}
	
	/**
	 *  This is used to upload big file using multipart upload.
	 *  By default, transfer manager uses 10 threads to upload big file in parts.
	 *  We can customize this behavior by using executor framework to pass number threads.
	 */
	@Bean
	public TransferManager transferManager() {
		return TransferManagerBuilder
				.standard()
				.withS3Client(amazonS3())
				.withMultipartUploadThreshold((long) 5 * 1024 * 1025)
				.withExecutorFactory(() -> Executors.newFixedThreadPool(5))
				.build();
	}
	
}
