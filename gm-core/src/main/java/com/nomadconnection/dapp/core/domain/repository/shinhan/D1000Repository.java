package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.D1000;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface D1000Repository extends JpaRepository<D1000, Long> {
    Optional<D1000> findTopByIdxCorpOrderByIdxDesc(Long idxCorp);

    D1000 findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);
}
