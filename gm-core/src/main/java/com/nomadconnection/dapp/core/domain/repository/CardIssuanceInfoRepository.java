package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardIssuanceInfoRepository extends JpaRepository<CardIssuanceInfo, Long> {
    Optional<CardIssuanceInfo> findTopByCorpAndDisabledTrueOrderByIdxDesc(Corp corp);
}
