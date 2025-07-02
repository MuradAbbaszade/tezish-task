package com.company.repository;

import com.company.model.DownloadToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface DownloadTokenRepository extends JpaRepository<DownloadToken, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DownloadToken> findByToken(String token);
}
