package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BrandConsentDto;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.ConsentDto;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.MismatchedException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.consent.ConsentMapping;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentMappingRepository;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository repoConsent;
    private final UserRepository repoUser;
    private final CommonCodeDetailRepository repoCodeDetail;
    private final ConsentMappingRepository repoConsentMapping;
    private final CardIssuanceInfoRepository repoCardIssuance;


    /**
     * 이용약관 현재 사용여부 등
     * 이용약관 목록
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity consents(String typeCode) {

        //todo 하드코딩 형태 수정 필요
        if (typeCode == null) {
            typeCode = "GOWID-A";
        }
        // List<BrandConsentDto> consents = repoConsent.findAllByEnabledOrderByConsentOrderAsc(true)
        List<BrandConsentDto> consents = repoConsent.findByEnabledAndTypeCodeOrderByConsentOrderAsc(true, typeCode)
                .map(BrandConsentDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(
                BusinessResponse.builder()
                        .data(consents)
                        .build());
    }

    public ResponseEntity consentCard(Long idxUser) {
        return ResponseEntity.ok().body(
                BusinessResponse.builder()
                        .data(repoCodeDetail.findAllByCode(CommonCodeType.GOWIDCARDS).stream().map(CardIssuanceDto.CardCompanyType::from).collect(Collectors.toList()))
                        .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity consentCardSave(Long idxUser, ConsentDto.RegisterCardUserConsent dto) {
        User user = repoUser.findById(idxUser).orElseThrow(
                () -> UserNotFoundException.builder().build()
        );

        user.cardCompany(dto.getCompanyCode());
        // 초기화된 유저인 경우
        if (user.isReset()) {
            user.isReset(false);
        }
        repoUser.save(user);

        // 이용약관 매핑
        for(ConsentDto.RegDto regDto : dto.getConsents()) {
            ConsentMapping consentMapping = repoConsentMapping.findByIdxUserAndIdxConsent(user.idx(), regDto.idxConsent);
            if (consentMapping == null) {
                repoConsentMapping.save(
                    ConsentMapping.builder()
                        .idxConsent(regDto.idxConsent)
                        .idxUser(idxUser)
                        .status(regDto.status)
                        .build()
                );
            } else {
                repoConsentMapping.save(
                    consentMapping.status(regDto.status)
                );
            }
        }

        CardIssuanceInfo cardIssuanceInfo = repoCardIssuance.findByUserAndCardType(user, CardType.GOWID).orElseThrow(
            () -> EntityNotFoundException.builder()
                .entity("CardIssuanceInfo")
                .build());

        if (!cardIssuanceInfo.idx().equals(dto.getCardIssuanceInfoIdx())) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        cardIssuanceInfo.updateCardCompany(dto.getCompanyCode());

        return ResponseEntity.ok().body(BusinessResponse.builder().data(cardIssuanceInfo).build());
    }
}
