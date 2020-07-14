package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.shinhan.D1200;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface D1200Repository extends JpaRepository<D1200, Long> {
    D1200 findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);

    Optional<D1200> findFirstByD007AndD008OrderByUpdatedAtDesc(String d007, String d008);

}
