package com.nomadconnection.dapp.api.v2.service.issuance;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardIssuanceInfoService {
	private final CardIssuanceInfoRepository cardIssuanceInfoRepository;

	public void saveCardIssuanceInfo(User user, Corp corp){
		CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoRepository.getTopByUserAndDisabledFalseOrderByIdxDesc(user);
		if (!ObjectUtils.isEmpty(cardIssuanceInfo)) {
			cardIssuanceInfoRepository.save(cardIssuanceInfo.corp(corp));
		}
	}
}
