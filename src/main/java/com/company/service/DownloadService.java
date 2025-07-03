package com.company.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DownloadService {
    private final PdfService pdfService;
    private final TokenService tokenService;

    @Transactional
    public ResponseEntity<?> generateDownloadLink(String userFullName, MultipartFile imageFile, String username) {
        try {
            String pdfPath = pdfService.generatePdfWithUserInfo(userFullName, imageFile);
            String downloadToken = tokenService.generateDownloadToken(pdfPath, username);
            return ResponseEntity.ok(java.util.Map.of(
                    "token", downloadToken,
                    "message", "PDF generated and download link created successfully"
            ));
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> downloadFile(String token) {
        try {
        String tokenUsername = tokenService.getTokenUsername(token);
        String pdfPath = tokenService.validateAndGetPdfPath(token);

        if (pdfPath == null || tokenUsername == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid, used, or expired token");
        }

        if (!pdfService.isPdfFileExists(pdfPath)) {
            return ResponseEntity.notFound().build();
        }

        return buildPdfDownloadResponse(pdfPath);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private ResponseEntity<Resource> buildPdfDownloadResponse(String pdfPath) {
        File file = new File(pdfPath);
        Resource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
