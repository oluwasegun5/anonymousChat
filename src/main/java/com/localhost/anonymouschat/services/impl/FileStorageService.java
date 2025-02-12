package com.localhost.anonymouschat.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
        Path targetLocation = Paths.get(uploadDir).resolve(fileName);
        // Validate file type and size
        // Store file
        return fileName;
    }
}