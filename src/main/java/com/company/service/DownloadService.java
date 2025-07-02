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
            String downloadLink = "/download/file?token=" + downloadToken;
            return ResponseEntity.ok(java.util.Map.of(
                    "downloadLink", downloadLink,
                    "message", "PDF generated and download link created successfully"
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error generating PDF: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> downloadFile(String token) {
        try {
            String tokenUsername = tokenService.getTokenUsername(token);
            if (isInvalidToken(tokenUsername, token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid, used, or expired token");
            }

            String pdfPath = tokenService.validateAndGetPdfPath(token, tokenUsername);
            if (!pdfService.isPdfFileExists(pdfPath)) {
                return ResponseEntity.notFound().build();
            }

            return buildPdfDownloadResponse(pdfPath);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }

    private boolean isInvalidToken(String username, String token) {
        return username == null || tokenService.validateAndGetPdfPath(token, username) == null;
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
