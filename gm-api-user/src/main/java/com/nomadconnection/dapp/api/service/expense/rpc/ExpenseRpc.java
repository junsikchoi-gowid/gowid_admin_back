package com.nomadconnection.dapp.api.service.expense.rpc;

import com.nomadconnection.dapp.api.config.ExpenseConfig;
import com.nomadconnection.dapp.api.service.expense.rpc.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseRpc {

    private final ExpenseConfig expenseConfig;
    private final CommonRpc commonRpc;

    public UserRes requestUser(String email) {
        String url = expenseConfig.getDomainUrl() + expenseConfig.getUserUrl() + "/" + email;
        return commonRpc.requestApi(url, HttpMethod.GET, getHeaderMap(), null, UserRes.class);
    }

    public UserSyncRes requestSyncUser(String email, String password) {
        String url = expenseConfig.getDomainUrl() + expenseConfig.getUserUrl() + "/sync";
        UserSyncReq userSyncReq = new UserSyncReq(email, password);
        return commonRpc.requestApi(url, HttpMethod.POST, null, userSyncReq, UserSyncRes.class);
    }

    public UserRes requestUpdateCredential(String email, String password) {
        String url = expenseConfig.getDomainUrl() + expenseConfig.getUserUrl() + "/" + email;
        UserCredentialReq userSyncReq = new UserCredentialReq(email, password);

        return commonRpc.requestApi(url, HttpMethod.PATCH, getHeaderMap(), userSyncReq, UserRes.class);
    }

    private Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("apiKey", expenseConfig.getApiKey());
        return headerMap;
    }

    private Map<String, String> getStatusHeaderMap() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("accessKey", expenseConfig.getAccessKey());
        return headerMap;
    }

    public ExpenseStatusRes requestStatus(String resCompanyIdentityNo) {
        String url = expenseConfig.getDomainUrl() + expenseConfig.getStatusUrl() + "/" + resCompanyIdentityNo;
        return commonRpc.requestApi(url, HttpMethod.GET, getStatusHeaderMap(), null, ExpenseStatusRes.class);
    }
}
