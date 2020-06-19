package com.nomadconnection.dapp.api.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.Base64;
import com.amazonaws.util.IOUtils;
import com.nomadconnection.dapp.api.config.AwsS3Config;
import com.nomadconnection.dapp.api.exception.ServerError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {

    private final AwsS3Config config;

    public String s3FileUpload(File file, String key) {
        try {
            AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                    //.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(config.accessKey(), config.secretKey())))
                    .withCredentials(new InstanceProfileCredentialsProvider(false))
                    .withRegion(Regions.AP_NORTHEAST_2)
                    .build();
            amazonS3.putObject(config.bucketName(), key, file);

            return config.bucketUrl() + key;

        } catch (Exception e) {
            log.error("([ s3FileUpload ]) $error='FAILED TO UPLOAD', $path='{}', $exception='{} => {}'", config.bucketUrl() + key, e.getClass().getSimpleName(), e.getMessage(), e);
            throw ServerError.builder().category(ServerError.Category.S3_SERVER_ERROR).build();
        }
    }

    public String s3FileDownload(String key) {
        try {
            AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                    //.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(config.accessKey(), config.secretKey())))
                    .withCredentials(new InstanceProfileCredentialsProvider(false))
                    .withRegion(Regions.AP_NORTHEAST_2)
                    .build();

            S3Object s3Object = amazonS3.getObject(new GetObjectRequest(config.bucketName(), key));
            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();

            return Base64.encodeAsString(IOUtils.toByteArray(objectInputStream));

        } catch (IOException e) {
            log.error("([ s3FileUpload ]) $error='FAILED TO DOWNLOAD', $path='{}', $exception='{} => {}'", config.bucketUrl() + key, e.getClass().getSimpleName(), e.getMessage(), e);
            throw ServerError.builder().category(ServerError.Category.S3_SERVER_ERROR).build();
        }
    }

    public void s3FileDelete(String key) {
        try {
            AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(config.accessKey(), config.secretKey())))
                    //.withCredentials(new InstanceProfileCredentialsProvider(false))
                    .withRegion(Regions.AP_NORTHEAST_2)
                    .build();

            amazonS3.deleteObject(new DeleteObjectRequest(config.bucketName(), key));

        } catch (Exception e) {
            log.error("([ s3FileDelete ]) $error='FAILED TO DELETE', $path='{}', $exception='{} => {}'", config.bucketUrl() + key, e.getClass().getSimpleName(), e.getMessage(), e);
            throw ServerError.builder().category(ServerError.Category.S3_SERVER_ERROR).build();
        }
    }
}
