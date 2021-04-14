package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.api.NotRegisteredException;
import com.nomadconnection.dapp.api.exception.v2.ResourceNotFoundException;
import com.nomadconnection.dapp.api.service.shinhan.D1200Service;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.kised.Kised;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.shinhan.D1200;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.nomadconnection.dapp.api.exception.v2.code.ErrorCode.RESOURCE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardIssuanceInfoService {

    private final CardIssuanceInfoRepository cardIssuanceInfoRepository;
    private final D1200Service d1200Service;
    private final CorpService corpService;

    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceInfo updateIssuanceStatus(Corp corp, CardType cardType, IssuanceStatus issuanceStatus) {
        CardIssuanceInfo cardIssuanceInfo
            = cardIssuanceInfoRepository.findByCorpAndCardType(corp, cardType)
            .orElseThrow(() -> new NoResultException());
        cardIssuanceInfo.issuanceStatus(issuanceStatus);
        updateLocalTime(cardIssuanceInfo, issuanceStatus);
        return cardIssuanceInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceInfo updateIssuanceStatus(Long idxCardIssuanceInfo, IssuanceStatus issuanceStatus) {
        CardIssuanceInfo cardIssuanceInfo
            = cardIssuanceInfoRepository.findByIdx(idxCardIssuanceInfo)
            .orElseThrow(() -> new NoResultException());

        cardIssuanceInfo.issuanceStatus(issuanceStatus);
        updateLocalTime(cardIssuanceInfo, issuanceStatus);
        return cardIssuanceInfo;
    }

    private void updateLocalTime(CardIssuanceInfo cardIssuanceInfo, IssuanceStatus issuanceStatus) {
        if (IssuanceStatus.APPLY.equals(issuanceStatus)) {
            cardIssuanceInfo.appliedAt(LocalDateTime.now());
        } else if (IssuanceStatus.ISSUED.equals(issuanceStatus)) {
            cardIssuanceInfo.issuedAt(LocalDateTime.now());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceInfo updateIssuanceStatusByApplicationDateAndNumber(String applicationDate, String applicationNum, CardType cardType, IssuanceStatus issuanceStatus) {
        Long corpIdx = d1200Service.getD1200ByApplicationDateAndApplicationNum(applicationDate, applicationNum).getIdxCorp();
        Corp corp = corpService.findByCorpIdx(corpIdx);
        return updateIssuanceStatus(corp, cardType, issuanceStatus);
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
    public void updateCorpByUser(User user, Corp corp, CardType cardType){
        Optional<CardIssuanceInfo> cardIssuanceInfo = cardIssuanceInfoRepository.findByUserAndCardType(user, cardType);
        cardIssuanceInfo.ifPresent(
            issuanceInfo -> issuanceInfo.corp(corp)
        );
    }

    @Transactional(readOnly = true)
    public CardIssuanceInfo findByUserOrElseThrow(User user, CardType cardType){
        return cardIssuanceInfoRepository.findByUserAndCardType(user, cardType).orElseThrow(
            () -> EntityNotFoundException.builder()
                .entity("CardIssuanceInfo")
                .build());
    }

    @Transactional(readOnly = true)
    public CardIssuanceInfo findByUserAndCardTypeOrDefaultEntity(User user, CardType cardType, CardIssuanceInfo defaultEntity){
        return cardIssuanceInfoRepository.findByUserAndCardType(user, cardType).orElse(defaultEntity);
    }

    @Transactional(readOnly = true)
    public CardIssuanceInfo findTopByCorp(Corp corp, CardType cardType){
        return cardIssuanceInfoRepository.findByCorpAndCardType(corp, cardType)
                .orElseThrow(() -> new NotRegisteredException(ErrorCode.Api.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public CardIssuanceInfo findByUserAndCardType(User user, CardType cardType){
        return cardIssuanceInfoRepository.findByUserAndCardType(user, cardType).orElseThrow(
            () -> new NotRegisteredException(ErrorCode.Api.NOT_FOUND)
        );
    }

    @Transactional(readOnly = true)
    public CardIssuanceInfo findById(Long cardIssuanceInfoIdx){
        return cardIssuanceInfoRepository.findById(cardIssuanceInfoIdx).orElseThrow(
            () -> new NotRegisteredException(ErrorCode.Api.NOT_FOUND)
        );
    }

    public boolean isIssuedCorp(IssuanceStatus issuanceStatus){
        return IssuanceStatus.ISSUED.equals(issuanceStatus);
    }

    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceInfo updateKised(Long cardIssuanceInfoIdx, Kised kised) throws Exception {

        CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoRepository.findByIdx(cardIssuanceInfoIdx)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getDesc(), "cardIssuanceInfoIdx : " + cardIssuanceInfoIdx));
        cardIssuanceInfo.kised(kised);
        return cardIssuanceInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceInfo updateAccount(Long cardIssuanceInfoIdx, String account) throws Exception {

        CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoRepository.findByIdx(cardIssuanceInfoIdx)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getDesc(), "cardIssuanceInfoIdx : " + cardIssuanceInfoIdx));
        cardIssuanceInfo.getBankAccount().setBankAccount(account);
        return cardIssuanceInfo;
    }

}
