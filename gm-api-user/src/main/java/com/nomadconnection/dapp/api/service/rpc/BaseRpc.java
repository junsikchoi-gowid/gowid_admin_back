package com.nomadconnection.dapp.api.service.rpc;

import com.nomadconnection.dapp.api.dto.shinhan.gateway.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.exception.BusinessException;
import com.nomadconnection.dapp.api.util.JsonUtil;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;


@Slf4j
@Component
public class BaseRpc {

    @Value("${gateway.idc.host}")
    private String GATEWAY_IDC_HOST;

    @Value("${gateway.idc.protocol}")
    private String GATEWAY_IDC_PROTOCOL;

    protected <T> T requestGateWayByJson(String gatewayUrl, HttpMethod httpMethod,
                                         Map<String, String> headerParams, Object bodyParams,
                                         Class<T> responseType,
                                         ShinhanGwApiType shinhanGwApiType) {
        HttpHeaders headers = makeHeader(headerParams);
        RestTemplate restTemplate = new RestTemplate();
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(60000);
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(90000);

        try {
            log.debug("Request [POST] {}", gatewayUrl);
            log.debug("## Request header ==> {}", JsonUtil.generateClassToJson(headers));
            log.debug("## Request body ==> {}", JsonUtil.generateClassToJson(bodyParams));
            ResponseEntity<T> response = restTemplate.exchange(gatewayUrl, httpMethod, new HttpEntity<>(bodyParams, headers), responseType);
            log.info("## Response ==> {}", JsonUtil.generateClassToJson(response));

            return response.getBody();

        } catch (RestClientResponseException e) {

            try {
                log.error("## Response ==> {}", JsonUtil.generateClassToJson(e.getResponseBodyAsString()));
            } catch (IOException ioException) {
                responseRpcExternalError(shinhanGwApiType, e);
            }
            responseRpcExternalError(shinhanGwApiType, e);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
            responseRpcInternalError(shinhanGwApiType, e);
        }
        return null;
    }

    private HttpHeaders makeHeader(Map<String, String> headerParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-host", GATEWAY_IDC_HOST);
        headers.add("x-protocol", GATEWAY_IDC_PROTOCOL);

        if (headerParams != null) {
            for (String key : headerParams.keySet()) {
                headers.add(key, headerParams.get(key));
            }
        }
        return headers;
    }

    private void responseRpcExternalError(ShinhanGwApiType shinhanGwApiType, Exception e) {
        log.error("error ====== {}", shinhanGwApiType.getName());

        if (ShinhanGwApiType.SH1000.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1000, e.getMessage());
        }
        if (ShinhanGwApiType.SH1100.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1100, e.getMessage());
        }
        if (ShinhanGwApiType.SH1200.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200, e.getMessage());
        }
        if (ShinhanGwApiType.SH1400.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1400, e.getMessage());
        }
        if (ShinhanGwApiType.SH1510.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1510, e.getMessage());
        }
        if (ShinhanGwApiType.SH1520.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1520, e.getMessage());
        }
        if (ShinhanGwApiType.SH1530.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1530, e.getMessage());
        }
        if (ShinhanGwApiType.SH1700.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1700, e.getMessage());
        }

        throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_GW, "["+shinhanGwApiType.getName()+"] "+e.getMessage());
    }

    private void responseRpcInternalError(ShinhanGwApiType shinhanGwApiType, Exception e) {
        log.error("error ====== {}", shinhanGwApiType.getName());

        if (ShinhanGwApiType.SH1000.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1000, e.getMessage());
        }
        if (ShinhanGwApiType.SH1100.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1100, e.getMessage());
        }
        if (ShinhanGwApiType.SH1200.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1200, e.getMessage());
        }
        if (ShinhanGwApiType.SH1400.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1400, e.getMessage());
        }
        if (ShinhanGwApiType.SH1510.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1510, e.getMessage());
        }
        if (ShinhanGwApiType.SH1520.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1520, e.getMessage());
        }
        if (ShinhanGwApiType.SH1530.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1530, e.getMessage());
        }
        if (ShinhanGwApiType.SH1700.getName().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1700, e.getMessage());
        }

        throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_GW, "["+shinhanGwApiType.getName()+"] "+e.getMessage());
    }

}
