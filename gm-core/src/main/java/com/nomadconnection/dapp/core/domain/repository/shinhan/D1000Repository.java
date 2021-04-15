package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface D1000Repository extends JpaRepository<D1000, Long> {
    D1000 getTopByIdxCorpOrderByIdxDesc(Long idxCorp);

    Optional<D1000> findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);

    Optional<D1000> findFirstByCardIssuanceInfoOrderByUpdatedAtDesc(CardIssuanceInfo cardIssuanceInfo);

    D1000 findFirstByD071AndD072OrderByUpdatedAtDesc(String d079, String d080);

    @Transactional
    @Modifying
    @Query("delete from D1000  where idxCorp = :idxCorp")
    void deleteByCorpIdx(@Param("idxCorp") Long idxCorp);

}
