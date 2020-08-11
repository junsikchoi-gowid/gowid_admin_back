package com.nomadconnection.dapp.core.domain.repository.common;

import com.nomadconnection.dapp.core.domain.common.SignatureHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignatureHistoryRepository extends JpaRepository<SignatureHistory, Long> {
    Optional<SignatureHistory> findFirstByApplicationDateAndApplicationNumOrderByUpdatedAtDesc(String applicationDate, String applicationNum);

    Optional<SignatureHistory> findFirstByUserIdxOrderByUpdatedAtDesc(long userIdx);
}
