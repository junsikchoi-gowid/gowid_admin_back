package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.shinhan.D1510;
import org.springframework.data.jpa.repository.JpaRepository;

public interface D1510Repository extends JpaRepository<D1510, Long> {
    D1510 findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);
}
