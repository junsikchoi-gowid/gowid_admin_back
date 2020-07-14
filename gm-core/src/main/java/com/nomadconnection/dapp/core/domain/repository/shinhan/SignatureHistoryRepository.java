package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.shinhan.SignatureHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignatureHistoryRepository extends JpaRepository<SignatureHistory, Long> {
    Optional<SignatureHistory> findFirstByApplicationDateAndApplicationNum(String applicationDate, String applicationNum);
}
