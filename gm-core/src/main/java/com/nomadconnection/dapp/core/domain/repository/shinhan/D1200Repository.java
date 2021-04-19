package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.shinhan.D1200;
import com.nomadconnection.dapp.core.domain.shinhan.D1400;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface D1200Repository extends JpaRepository<D1200, Long> {
    Optional<D1200> findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);

    Optional<D1200> findFirstByD007AndD008OrderByUpdatedAtDesc(String d007, String d008);

    Optional<D1200> findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(CardIssuanceInfo cardIssuanceInfo);

    @Transactional
    @Modifying
    @Query("delete from D1200  where idxCorp = :idxCorp")
    void deleteByCorpIdx(@Param("idxCorp") Long idxCorp);
}
