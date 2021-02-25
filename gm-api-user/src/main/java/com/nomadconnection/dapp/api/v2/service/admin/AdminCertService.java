package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.core.domain.res.ConnectedMngRepository;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCertService {
    private final ConnectedMngRepository connectedMngRepository;

    @Transactional(readOnly = true)
    public ResponseEntity getCertList(Long idxUser) {
        return ResponseEntity.ok().body(BusinessResponse.builder().data(
            connectedMngRepository.findIdxUser(idxUser)
        ).build());
    }
}
