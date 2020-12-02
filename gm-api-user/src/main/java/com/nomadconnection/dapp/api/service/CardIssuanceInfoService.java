package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.persistence.NoResultException;

import static com.nomadconnection.dapp.api.dto.gateway.ApiResponse.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardIssuanceInfoService {

    private final CardIssuanceInfoRepository cardIssuanceInfoRepository;

    public ApiResponse<CardIssuanceInfo> updateIssuanceStatus(Corp corp, IssuanceStatus issuanceStatus) throws Exception {
        CardIssuanceInfo cardIssuanceInfo
            = cardIssuanceInfoRepository.findByCorp(corp)
                                        .orElseThrow(() -> new NoResultException());
        cardIssuanceInfo.issuanceStatus(issuanceStatus);
        return OK(cardIssuanceInfo);
    }

    public void saveCardIssuanceInfo(User user, Corp corp){
        CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoRepository.getTopByUserAndDisabledFalseOrderByIdxDesc(user);
        if (!ObjectUtils.isEmpty(cardIssuanceInfo)) {
            cardIssuanceInfoRepository.save(cardIssuanceInfo.corp(corp));
        }
    }

}
