package com.nomadconnection.dapp.api.service.external.rpc;

import com.nomadconnection.dapp.api.config.QuotabookConfig;
import com.nomadconnection.dapp.api.dto.external.quotabook.RoundingReq;
import com.nomadconnection.dapp.api.dto.external.quotabook.RoundingRes;
import com.nomadconnection.dapp.api.dto.external.quotabook.ShareClassesRes;
import com.nomadconnection.dapp.api.dto.external.quotabook.StakeholdersRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotabookRpc {

    private final QuotabookConfig quotabookConfig;
    private final CommonRpc commonRpc;

    public StakeholdersRes requestStakeHolders(String apiKey, HttpMethod httpMethod) {
        Map<String, String> headerMap = getHeaderMap(apiKey);
        return commonRpc.requestApi(quotabookConfig.getDomainUrl() + quotabookConfig.getStakeholdersUrl(), httpMethod,
                headerMap, null, StakeholdersRes.class);
    }

    public ShareClassesRes requestShareClasses(String apiKey, HttpMethod httpMethod) {
        Map<String, String> headerMap = getHeaderMap(apiKey);
        return commonRpc.requestApi(quotabookConfig.getDomainUrl() + quotabookConfig.getShareClassesUrl(), httpMethod,
                headerMap, null, ShareClassesRes.class);
    }

    public RoundingRes requestRounding(String apiKey, HttpMethod httpMethod, RoundingReq request) {
        Map<String, String> headerMap = getHeaderMap(apiKey);
        String url = quotabookConfig.getDomainUrl() + quotabookConfig.getRoundingUrl()
                + "?"
                + "current=" + request.getCurrent() + "&"
                + "pageSize=" + request.getPageSize();

        return commonRpc.requestApi(url, httpMethod, headerMap, request, RoundingRes.class);
    }

    private Map<String, String> getHeaderMap(String apiKey) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + apiKey);
        return headerMap;
    }


}
