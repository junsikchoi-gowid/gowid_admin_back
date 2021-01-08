package com.nomadconnection.dapp.api.service.expense.rpc;

import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.api.service.common.CommonRpcService;
import com.nomadconnection.dapp.api.util.JsonUtil;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;


@Slf4j
@Component("Expense.CommonRpc")
@RequiredArgsConstructor
public class CommonRpc {

    private final CommonRpcService commonRpcService;

    protected <T> T requestApi(String url, HttpMethod httpMethod,
                               Map<String, String> headerParams, Object bodyParams,
                               Class<T> responseType) {

        HttpHeaders headers = commonRpcService.makeHeader(headerParams);
        RestTemplate restTemplate = new RestTemplate();
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(20000);
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(200000);

        try {
            ResponseEntity<T> response = commonRpcService.requestApi(url, httpMethod, bodyParams, responseType, headers, restTemplate);
            return response.getBody();

        } catch (RestClientResponseException e) {

            try {
                log.warn("## Error Response ==> {}", JsonUtil.generateClassToJson(e.getResponseBodyAsString()));
                return null;

            } catch (IOException ioException) {
                throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_EXPENSE, ioException.getMessage());
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SystemException(ErrorCode.External.INTERNAL_ERROR_EXPENSE, e.getMessage());
        }
    }


}
