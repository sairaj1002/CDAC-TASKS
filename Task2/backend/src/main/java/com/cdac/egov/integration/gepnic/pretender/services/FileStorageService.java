package com.cdac.egov.integration.gepnic.pretender.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.cdac.egov.integration.gepnic.pretender.forms.UploadedFileResponse;

@Service
public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private final Path uploadRoot;
    public FileStorageService(@Value("${pretender.upload-dir:uploads}") String uploadDir) {
        uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }
    public UploadedFileResponse store(MultipartFile file) {
        if (file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a file first");
        String originalName = Path.of(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename()).getFileName().toString();
        String extension = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) extension = originalName.substring(dot).replaceAll("[^A-Za-z0-9.]", "");
        String storedName = UUID.randomUUID() + extension;
        try {
            Files.createDirectories(uploadRoot);
            Files.copy(file.getInputStream(), uploadRoot.resolve(storedName));
            return new UploadedFileResponse(storedName, originalName);
        } catch (IOException ex) {
            deleteQuietly(storedName);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store uploaded file", ex);
        }
    }

    public void deleteQuietly(String fileName) {
        if (fileName == null || !Path.of(fileName).getFileName().toString().equals(fileName)) return;
        try {
            Files.deleteIfExists(uploadRoot.resolve(fileName).normalize());
        } catch (IOException ex) {
            logger.warn("Could not remove staged upload {} after a failed tender save", fileName, ex);
        }
    }
    public Resource load(String fileName) {
        if (!Path.of(fileName).getFileName().toString().equals(fileName)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file name");
        try {
            Path path = uploadRoot.resolve(fileName).normalize();
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            return resource;
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found", ex);
        }
    }
}
