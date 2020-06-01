package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.D1100;
import org.springframework.data.jpa.repository.JpaRepository;

public interface D1100Repository extends JpaRepository<D1100, Long> {
    D1100 getTopByIdxCorpOrderByIdxDesc(Long idxCorp);

    D1100 findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);
}
