package com.nomadconnection.dapp.api.service.external;

import com.nomadconnection.dapp.api.dto.external.ExternalLinkReq;
import com.nomadconnection.dapp.core.domain.external.UserExternalLink;
import com.nomadconnection.dapp.core.domain.repository.external.UserExternalLinkRepo;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.security.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalLinkService {

    private final UserExternalLinkRepo userExternalLinkRepo;

    public void saveExternalKey(ExternalLinkReq request, CustomUser customUser) {

        UserExternalLink userExternalLink = userExternalLinkRepo.findByUserAndExternalCompanyType(
                User.builder().idx(customUser.idx()).build(),
                request.getExternalCompanyType())
                .orElse(
                        UserExternalLink.builder()
                                .user(User.builder().idx(customUser.idx()).build())
                                .externalCompanyType(request.getExternalCompanyType())
                                .build()
                );

        userExternalLink.setExternalKey(request.getExternalKey());
        userExternalLinkRepo.save(userExternalLink);
    }
}
