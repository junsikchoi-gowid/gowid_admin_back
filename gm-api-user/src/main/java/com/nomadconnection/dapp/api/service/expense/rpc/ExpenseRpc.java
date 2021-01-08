package com.nomadconnection.dapp.api.service.expense.rpc;

import com.nomadconnection.dapp.api.config.ExpenseConfig;
import com.nomadconnection.dapp.api.service.expense.rpc.dto.UserRes;
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

    private Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("apiKey", expenseConfig.getApiKey());
        return headerMap;
    }

}
