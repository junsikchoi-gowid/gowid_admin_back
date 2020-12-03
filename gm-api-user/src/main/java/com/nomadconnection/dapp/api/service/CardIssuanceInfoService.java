package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.service.shinhan.D1200Service;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardIssuanceInfoService {

    private final CardIssuanceInfoRepository cardIssuanceInfoRepository;
    private final D1200Service d1200Service;
    private final CorpService corpService;

    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceInfo updateIssuanceStatus(Corp corp, IssuanceStatus issuanceStatus) {
        CardIssuanceInfo cardIssuanceInfo
            = cardIssuanceInfoRepository.findByCorpAndDisabledFalseOrderByIdxDesc(corp)
                                        .orElseThrow(() -> new NoResultException());
        cardIssuanceInfo.issuanceStatus(issuanceStatus);
        return cardIssuanceInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceInfo updateIssuanceStatus(Long idxUser, IssuanceStatus issuanceStatus) {
        CardIssuanceInfo cardIssuanceInfo
            = cardIssuanceInfoRepository.findTopByUserAndDisabledFalseOrderByIdxDesc(idxUser)
            .orElseThrow(() -> new NoResultException());

        cardIssuanceInfo.issuanceStatus(issuanceStatus);

        return cardIssuanceInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceInfo updateIssuanceStatusByApplicationDateAndNumber(String applicationDate, String applicationNum, IssuanceStatus issuanceStatus) {
        Long corpIdx = d1200Service.getD1200ByApplicationDateAndApplicationNum(applicationDate, applicationNum).getIdxCorp();
        Corp corp = corpService.findByCorpIdx(corpIdx);
        return updateIssuanceStatus(corp, issuanceStatus);
    }

    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceInfo saveCardIssuanceInfo(CardIssuanceInfo cardIssuanceInfo){
        return cardIssuanceInfoRepository.save(cardIssuanceInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCardIssuanceInfo(CardIssuanceInfo cardIssuanceInfo){
        cardIssuanceInfoRepository.delete(cardIssuanceInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCorpByUser(User user, Corp corp){
        Optional<CardIssuanceInfo> cardIssuanceInfo = findTopByUser(user);
        cardIssuanceInfo.ifPresent(
            issuanceInfo -> issuanceInfo.corp(corp)
        );
    }

    @Transactional(readOnly = true)
    public Optional<CardIssuanceInfo> findTopByUser(User user){
        return cardIssuanceInfoRepository.findTopByUserAndDisabledFalseOrderByIdxDesc(user);
    }

}
