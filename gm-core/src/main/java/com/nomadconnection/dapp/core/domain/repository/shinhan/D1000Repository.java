package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import org.springframework.data.jpa.repository.JpaRepository;

public interface D1000Repository extends JpaRepository<D1000, Long> {
    D1000 getTopByIdxCorpOrderByIdxDesc(Long idxCorp);

    D1000 findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);

    D1000 findFirstByD071AndD072OrderByUpdatedAtDesc(String d079, String d080);

}
