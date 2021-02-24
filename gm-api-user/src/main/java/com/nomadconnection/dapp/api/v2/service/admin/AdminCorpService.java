package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.v2.dto.AdminDto.CorpDto;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import com.nomadconnection.dapp.core.domain.res.ConnectedMngRepository;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCorpService {
    private final CorpRepository corpRepository;
    private final ConnectedMngRepository connectedMngRepository;

    @Transactional(readOnly = true)
    public ResponseEntity getCorpList(String keyWord, Pageable pageable) {
        Page<CorpCustomRepository.CorpListDto> corpList = corpRepository.adminCorpListV2(keyWord, pageable);
        return ResponseEntity.ok().body(
            BusinessResponse.builder().data(corpList).build()
        );
    }

    @Transactional(readOnly = true)
    public ResponseEntity getCorpInfo(Long idxCorp) {
        CorpDto corpDto = corpRepository.findById(idxCorp).map(CorpDto::from).orElseThrow(
            () -> CorpNotRegisteredException.builder().build()
        );

        return ResponseEntity.ok().body(BusinessResponse.builder()
            .data(corpDto)
            .build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity getCertList(Long idxUser) {
        return ResponseEntity.ok().body(BusinessResponse.builder().data(
            connectedMngRepository.findIdxUser(idxUser)
        ).build());
    }
}
