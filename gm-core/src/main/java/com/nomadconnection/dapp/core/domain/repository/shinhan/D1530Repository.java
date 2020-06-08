package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.D1530;
import org.springframework.data.jpa.repository.JpaRepository;

public interface D1530Repository extends JpaRepository<D1530, Long> {
    D1530 findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);
}
