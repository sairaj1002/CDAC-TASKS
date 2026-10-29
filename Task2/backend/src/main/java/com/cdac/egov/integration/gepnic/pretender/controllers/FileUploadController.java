package com.cdac.egov.integration.gepnic.pretender.controllers;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.cdac.egov.integration.gepnic.pretender.forms.UploadedFileResponse;
import com.cdac.egov.integration.gepnic.pretender.services.FileStorageService;

@RestController
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*")
public class FileUploadController {
    private final FileStorageService files;
    public FileUploadController(FileStorageService files) { this.files = files; }

    @PostMapping("/api/files")
    public UploadedFileResponse upload(@RequestParam("file") MultipartFile file) { return files.store(file); }

    @GetMapping("/api/files/{fileName}")
    public ResponseEntity<Resource> download(@PathVariable String fileName) {
        Resource resource = files.load(fileName);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName.replace("\"", "") + "\"")
            .body(resource);
    }
}