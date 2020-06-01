package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.D1100;
import com.nomadconnection.dapp.core.domain.D1200;
import org.springframework.data.jpa.repository.JpaRepository;

public interface D1200Repository extends JpaRepository<D1100, Long> {
    D1200 findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);
}
