package com.company.controller;

import com.company.service.DownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class DownloadController {
    private final DownloadService downloadService;

    @PostMapping("/generate-link")
    public ResponseEntity<?> generateDownloadLink(@RequestParam String userFullName,
                                                  @RequestParam(required = false) MultipartFile imageFile) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return downloadService.generateDownloadLink(userFullName, imageFile, username);
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RequestParam String token) {
        return downloadService.downloadFile(token);
    }
}
