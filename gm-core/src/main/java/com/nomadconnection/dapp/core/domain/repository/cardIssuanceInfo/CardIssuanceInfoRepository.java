package com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardIssuanceInfoRepository extends JpaRepository<CardIssuanceInfo, Long> {
    Optional<CardIssuanceInfo> findTopByCorpAndDisabledFalseOrderByIdxDesc(Corp corp);

    Optional<CardIssuanceInfo> findByIdx(Long idx);
}
