package com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardIssuanceInfoRepository extends JpaRepository<CardIssuanceInfo, Long> {
    Optional<CardIssuanceInfo> findTopByUserAndDisabledFalseOrderByIdxDesc(User user);

    CardIssuanceInfo getTopByUserAndDisabledFalseOrderByIdxDesc(User user);
    Optional<CardIssuanceInfo> findByIdx(Long idx);

}
