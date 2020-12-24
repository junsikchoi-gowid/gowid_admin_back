package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerService {

    private final UserService service;

    @Transactional
    public Corp getIdxCorp(String externalId) {
        User user = service.findByExternalId(externalId);

        if(externalId.equals(user.externalId())) {
            return null;
        }

        Corp corp = user.corp();

        if(corp == null) {
            return null;
        }

        log.debug("idxUser {}", corp.user().idx());

        return corp;
    }
}
