package com.learning.SpringBootS3.controller;

import com.learning.SpringBootS3.service.DocumentService;
import com.learning.SpringBootS3.service.SQSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DocumentController {

    @Autowired
    private final DocumentService documentService;

    @Autowired
    private final SQSService sqsService;

    public DocumentController(DocumentService documentService, SQSService sqsService) {
        this.documentService = documentService;
        this.sqsService=sqsService;
    }

    @PostMapping
    public String saveImage(@RequestParam("file") MultipartFile file) {
        String fileURL=documentService.upload(file);
        sqsService.postFilePath(fileURL);
        return fileURL;
    }
}
