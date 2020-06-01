package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.D1520;
import org.springframework.data.jpa.repository.JpaRepository;

public interface D1520Repository extends JpaRepository<D1520, Long> {
    D1520 findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);
}
