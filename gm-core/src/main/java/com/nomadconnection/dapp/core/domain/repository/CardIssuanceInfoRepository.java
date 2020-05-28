package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.Corp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardIssuanceInfoRepository extends JpaRepository<CardIssuanceInfo, Long> {
    Optional<CardIssuanceInfo> findByCorpAndDisabledTrue(Corp corp);
}
