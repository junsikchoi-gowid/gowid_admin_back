package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.SignatureHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignatureHistoryRepository extends JpaRepository<SignatureHistory, Long> {

}
