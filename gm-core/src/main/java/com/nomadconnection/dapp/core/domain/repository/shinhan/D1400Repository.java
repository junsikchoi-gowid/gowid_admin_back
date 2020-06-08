package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.D1400;
import org.springframework.data.jpa.repository.JpaRepository;

public interface D1400Repository extends JpaRepository<D1400, Long> {
    D1400 findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);
}
