package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.shinhan.D1400;
import org.springframework.data.jpa.repository.JpaRepository;

public interface D1400Repository extends JpaRepository<D1400, Long> {
    D1400 findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);

    D1400 findFirstByD025AndD026OrderByUpdatedAtDesc(String d025, String d026);

    D1400 getTopByIdxCorpOrderByIdxDesc(Long idxCorp);
}
