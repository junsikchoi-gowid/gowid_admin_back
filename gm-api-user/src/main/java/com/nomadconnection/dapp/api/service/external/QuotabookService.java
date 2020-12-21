package com.nomadconnection.dapp.api.service.external;

import com.nomadconnection.dapp.api.dto.external.quotabook.RoundingReq;
import com.nomadconnection.dapp.api.dto.external.quotabook.RoundingRes;
import com.nomadconnection.dapp.api.dto.external.quotabook.ShareClassesRes;
import com.nomadconnection.dapp.api.dto.external.quotabook.StakeholdersRes;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.external.rpc.QuotabookRpc;
import com.nomadconnection.dapp.core.domain.external.ExternalCompanyType;
import com.nomadconnection.dapp.core.domain.external.UserExternalLink;
import com.nomadconnection.dapp.core.domain.repository.external.UserExternalLinkRepo;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.security.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotabookService {

    private final UserExternalLinkRepo userExternalLinkRepo;
    private final QuotabookRpc quotabookRpc;

    public StakeholdersRes getStakeHolders(CustomUser customUser) {
        UserExternalLink userExternalLink = getUserExternalLink(customUser);
        return quotabookRpc.requestStakeHolders(userExternalLink.getExternalKey(), HttpMethod.GET);
    }

    public ShareClassesRes getShareClasses(CustomUser customUser) {
        UserExternalLink userExternalLink = getUserExternalLink(customUser);
        return quotabookRpc.requestShareClasses(userExternalLink.getExternalKey(), HttpMethod.GET);
    }

    public RoundingRes getRounding(CustomUser customUser, RoundingReq request) {
        UserExternalLink userExternalLink = getUserExternalLink(customUser);
        return quotabookRpc.requestRounding(userExternalLink.getExternalKey(), HttpMethod.GET, request);
    }

    private UserExternalLink getUserExternalLink(CustomUser customUser) {
        return userExternalLinkRepo.findByUserAndExternalCompanyType(
                User.builder()
                        .idx(customUser.idx())
                        .build(),
                ExternalCompanyType.QUOTABOOK
        ).orElseThrow(() -> new BadRequestException(ErrorCode.Api.NOT_FOUND, "User api key is not exist"));
    }

}