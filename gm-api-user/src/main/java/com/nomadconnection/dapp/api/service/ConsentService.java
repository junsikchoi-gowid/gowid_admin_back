package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.BrandConsentDto;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.ConsentDto;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.MismatchedException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.consent.Consent;
import com.nomadconnection.dapp.core.domain.consent.ConsentMapping;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentMappingRepository;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorResponse;
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

    /**
     * 이용약관 단건 저장
     * idx 키값이 있으면 수정됨
     *
     * @param user,contents,enabled,essential,version,idx
     * @return body success , 정상처리
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<BusinessResponse> postConsent(org.springframework.security.core.userdetails.User user, BrandConsentDto dto) {

        //	권한
        if (user.getAuthorities().stream().anyMatch(o -> o.getAuthority().equals(Role.ROLE_MASTER.toString()))) {
            BrandConsentDto.from(repoConsent.save(Consent.builder()
                    .title(dto.title)
                    .contents(dto.contents)
                    .enabled(dto.enabled)
                    .essential(dto.essential)
                    .version(dto.version)
                    .idx(dto.idx)
                    .consentOrder(dto.consentOrder)
                    .corpStatus(dto.corpStatus)
                    .build()));
        } else {
            if (log.isErrorEnabled()) {
                log.error("([ postConsent ]) auth check , $user='{}', $dto='{}'", user, dto);
            }
            throw new RuntimeException("마스터 권한이 없음");
        }

        return ResponseEntity.ok().body(BusinessResponse.builder().build());
    }

    /**
     * 이용약관 단건 삭제
     *
     * @param idx, user
     * @return body success , 정상처리
     */

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ErrorResponse> consentDel(org.springframework.security.core.userdetails.User user, Long idx) {

        if (user.getAuthorities().stream().anyMatch(o -> o.getAuthority().equals(Role.ROLE_MASTER.toString()))) {

            repoConsent.deleteById(idx);
        } else {
            if (log.isErrorEnabled()) {
                log.error("([ postConsent ]) auth check , $user='{}', $key='{}'", user, idx);
            }
            throw new RuntimeException("마스터 권한이 없음");
        }

        return ResponseEntity.ok().body(ErrorResponse.from("success", "정상처리"));
    }

    public ResponseEntity consentCard(Long idxUser) {
        return ResponseEntity.ok().body(
                BusinessResponse.builder()
                        .data(repoCodeDetail.findAllByCode(CommonCodeType.GOWIDCARDS).stream().map(CardIssuanceDto.CardType::from).collect(Collectors.toList()))
                        .build());
    }

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

        CardIssuanceInfo cardIssuanceInfo = repoCardIssuance.findTopByUserAndDisabledFalseOrderByIdxDesc(user).orElseThrow(
            () -> EntityNotFoundException.builder()
                .entity("CardIssuanceInfo")
                .build());

        if (!cardIssuanceInfo.idx().equals(dto.getCardIssuanceInfoIdx())) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        cardIssuanceInfo = repoCardIssuance.save(cardIssuanceInfo
                .user(user)
                .cardCompany(dto.getCompanyCode()));

        return ResponseEntity.ok().body(BusinessResponse.builder().data(cardIssuanceInfo).build());
    }
}
