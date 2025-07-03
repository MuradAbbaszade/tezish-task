package com.company.service;

import com.company.model.DownloadToken;
import com.company.model.User;
import com.company.repository.DownloadTokenRepository;
import com.company.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    @Value("${download.token.expiry}")
    private int tokenExpiryMinutes;

    private final DownloadTokenRepository downloadTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public String generateDownloadToken(String pdfPath, String username) {
        String token = UUID.randomUUID().toString();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        User user = userOpt.get();
        DownloadToken downloadToken = new DownloadToken();
        downloadToken.setToken(token);
        downloadToken.setUser(user);
        downloadToken.setPdfPath(pdfPath);
        downloadToken.setExpiryTime(ZonedDateTime.now(ZoneId.of("Asia/Baku")).toLocalDateTime().plusMinutes(tokenExpiryMinutes));
        downloadTokenRepository.save(downloadToken);
        return token;
    }

    @Transactional
    public String validateAndGetPdfPath(String token) {
        return downloadTokenRepository.findByToken(token)
                .filter(t -> !t.isUsed())
                .filter(t -> LocalDateTime.now().isBefore(t.getExpiryTime()))
                .map(t -> {
                    t.setUsed(true);
                    downloadTokenRepository.save(t);
                    return t.getPdfPath();
                })
                .orElse(null);
    }

    public String getTokenUsername(String token) {
        Optional<DownloadToken> optToken = downloadTokenRepository.findByToken(token);
        return optToken.map(t -> t.getUser().getUsername()).orElse(null);
    }
}
