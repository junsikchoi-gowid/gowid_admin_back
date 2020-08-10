package com.nomadconnection.dapp.core.domain.repository.corp;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CeoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CeoInfoRepository extends JpaRepository<CeoInfo, Long> {
	CeoInfo getByCardIssuanceInfo(CardIssuanceInfo cardIssuanceInfo);
}
