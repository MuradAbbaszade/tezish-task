package com.company.controller;

import com.company.service.DownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class DownloadController {
    private final DownloadService downloadService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generateDownloadLink(@RequestParam String userFullName,
                                                  @RequestPart(required = false) MultipartFile imageFile) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return downloadService.generateDownloadLink(userFullName, imageFile, username);
    }

    @GetMapping
    public ResponseEntity<?> downloadFile(@RequestParam String token) {
        return downloadService.downloadFile(token);
    }
}
