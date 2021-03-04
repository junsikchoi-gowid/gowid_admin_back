package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.v2.dto.AdminDto;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CardIssunaceInfoCustomRepository;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCardIssuanceService {
    private final CardIssuanceInfoRepository cardIssuanceInfoRepository;

    @Transactional(readOnly = true)
    public CardIssunaceInfoCustomRepository.CardIssuanceInfoDto getIssuanceInfo(Long idxCardIssuanceInfo) {
        return cardIssuanceInfoRepository.issuanceInfo(idxCardIssuanceInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity updateIssuanceStatus(Long idxCardIssuanceInfo, AdminDto.UpdateIssuanceStatusDto dto) {
        CardIssuanceInfo issuanceInfo = cardIssuanceInfoRepository.findByIdx(idxCardIssuanceInfo).orElseThrow(
            () -> EntityNotFoundException.builder()
                .entity("CardIssuanceInfo")
                .build()
        );

        cardIssuanceInfoRepository.save(issuanceInfo.issuanceStatus(dto.getIssuanceStatus()));

        return ResponseEntity.ok().body(
            BusinessResponse.builder().build()
        );
    }
}
