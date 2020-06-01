package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.D1000;
import org.springframework.data.jpa.repository.JpaRepository;

public interface D1000Repository extends JpaRepository<D1000, Long> {
    D1000 getTopByIdxCorpOrderByIdxDesc(Long idxCorp);
}
