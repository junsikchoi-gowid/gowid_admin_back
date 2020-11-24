package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.corp.ExternalCorp;
import com.nomadconnection.dapp.core.domain.repository.corp.ExternalCorpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerService {

    private final ExternalCorpRepository repoExternalCorp;

    @Transactional
    public Corp getIdxCorp(String externalId) {
        ExternalCorp externalCorp = repoExternalCorp.findByExternalId(externalId);

        if(externalCorp == null)
            return null;

        Corp corp = externalCorp.corp();

        log.debug("idxUser {}", corp.user().idx());

        return corp;
    }
}
