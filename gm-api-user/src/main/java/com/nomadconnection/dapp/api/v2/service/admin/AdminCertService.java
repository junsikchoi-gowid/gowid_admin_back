package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.core.domain.repository.connect.ConnectedMngRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCertService {
    private final ConnectedMngRepository connectedMngRepository;

    @Transactional(readOnly = true)
    public List<ConnectedMngRepository.ConnectedMngDto> getCertList(Long idxUser) {
        return connectedMngRepository.findIdxUser(idxUser);
    }
}
