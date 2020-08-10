package com.nomadconnection.dapp.api.service.shinhan.rpc;

import com.nomadconnection.dapp.api.dto.shinhan.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.exception.BusinessException;
import com.nomadconnection.dapp.api.util.JsonUtil;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
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

    protected <T> T requestGateWayByJson(String gatewayUrl, HttpMethod httpMethod,
                                         Map<String, String> headerParams, Object bodyParams,
                                         Class<T> responseType,
                                         ShinhanGwApiType shinhanGwApiType) {
        HttpHeaders headers = makeHeader(headerParams);
        RestTemplate restTemplate = new RestTemplate();
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(60000);
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(600000);

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
            log.error("([ BaseRpc.requestGateWayByJson ]) $exception='{} => {}'", e.getMessage(), e);
            responseRpcInternalError(shinhanGwApiType, e);
        }
        return null;
    }

    private HttpHeaders makeHeader(Map<String, String> headerParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (headerParams != null) {
            for (String key : headerParams.keySet()) {
                headers.add(key, headerParams.get(key));
            }
        }
        return headers;
    }

    private void responseRpcExternalError(ShinhanGwApiType shinhanGwApiType, Exception e) {
        log.error("error ====== {}({})", shinhanGwApiType.getName(), shinhanGwApiType.getCode());

        if (ShinhanGwApiType.SH1000.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1000, e.getMessage());
        }
        if (ShinhanGwApiType.SH1100.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1100, e.getMessage());
        }
        if (ShinhanGwApiType.SH1200.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200, e.getMessage());
        }
        if (ShinhanGwApiType.SH1400.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1400, e.getMessage());
        }
        if (ShinhanGwApiType.SH1510.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1510, e.getMessage());
        }
        if (ShinhanGwApiType.SH1520.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1520, e.getMessage());
        }
        if (ShinhanGwApiType.SH1530.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1530, e.getMessage());
        }
        if (ShinhanGwApiType.SH1700.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1700, e.getMessage());
        }

        throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_GW, "["+shinhanGwApiType.getName()+"] "+e.getMessage());
    }

    private void responseRpcInternalError(ShinhanGwApiType shinhanGwApiType, Exception e) {
        log.error("error ====== {}({})", shinhanGwApiType.getName(), shinhanGwApiType.getCode());

        if (ShinhanGwApiType.SH1000.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1000, e.getMessage());
        }
        if (ShinhanGwApiType.SH1100.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1100, e.getMessage());
        }
        if (ShinhanGwApiType.SH1200.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1200, e.getMessage());
        }
        if (ShinhanGwApiType.SH1400.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1400, e.getMessage());
        }
        if (ShinhanGwApiType.SH1510.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1510, e.getMessage());
        }
        if (ShinhanGwApiType.SH1520.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1520, e.getMessage());
        }
        if (ShinhanGwApiType.SH1530.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1530, e.getMessage());
        }
        if (ShinhanGwApiType.SH1700.getCode().equals(shinhanGwApiType.getCode())) {
            throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_SHINHAN_1700, e.getMessage());
        }

        throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_GW, "["+shinhanGwApiType.getName()+"] "+e.getMessage());
    }

}
