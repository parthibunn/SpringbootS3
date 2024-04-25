package com.learning.SpringBootS3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.learning.SpringBootS3.config.AWSClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
public class DocumentService {


    @Autowired
    private final AWSClientConfig awsClientConfig;
    @Autowired
    private final AmazonS3 amazonS3;

    public DocumentService(AWSClientConfig awsClientConfig, AmazonS3 amazonS3) {
        this.awsClientConfig = awsClientConfig;
        this.amazonS3 = amazonS3;
    }

    public String upload(MultipartFile file) {
        File localFile = convertMultipartFileToFile(file);

        amazonS3.putObject(new PutObjectRequest(awsClientConfig.getBucketName(), file.getOriginalFilename(), localFile));

        return amazonS3.getUrl(awsClientConfig.getBucketName(), file.getOriginalFilename()).toString();
    }

    private File convertMultipartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try {
            Files.copy(file.getInputStream(), convertedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return convertedFile;
    }

    public void downloadFile(String fileURL) throws URISyntaxException, IOException {
        URI fileToBeDownloaded = new URI(fileURL);

        AmazonS3URI s3URI = new AmazonS3URI(fileToBeDownloaded);

        S3Object s3Object = amazonS3.getObject(s3URI.getBucket(), s3URI.getKey());
        InputStream fileInputStream = s3Object.getObjectContent();
        File file = new File("D:\\LivewireProjects\\SpringBootS3\\newFile.pdf");
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[8192];
            while ((read = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }

}
