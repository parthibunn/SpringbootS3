package com.learning.SpringBootS3.service;

import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.S3Object;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class SQSService {

    @Autowired
    SqsTemplate sqsTemplate;

    @Autowired
    DocumentService documentService;

    public void postFilePath(String fileURL) {
        sqsTemplate.send(sqsSendOptions ->
                sqsSendOptions
                        .queue("FileQueue")
                        .payload(fileURL)
        );
    }

    @SqsListener("FileQueue" )
    public void listen(Message<?> message) throws URISyntaxException, IOException {
        String fileURL = message.getPayload().toString();
        System.out.println("listen - " + fileURL);
        documentService.downloadFile(fileURL);
    }
}
