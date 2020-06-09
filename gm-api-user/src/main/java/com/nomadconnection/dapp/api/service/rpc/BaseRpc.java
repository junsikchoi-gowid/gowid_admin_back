package com.nomadconnection.dapp.api.service.rpc;

import com.nomadconnection.dapp.api.dto.shinhan.gateway.response.ApiResponse;
import com.nomadconnection.dapp.api.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class BaseRpc {

    @Value("${gateway.idc.host}")
    private String GATEWAY_IDC_HOST;

    @Value("${gateway.idc.protocol}")
    private String GATEWAY_IDC_PROTOCOL;

    protected <T> ApiResponse<T> requestGateway(String url,
                                             HttpMethod httpMethod,
                                             Map<String, String> headerParams,
                                             Object bodyParams,
                                             Class<T> responseType) throws IOException {

        HttpHeaders headers = makeHeader(headerParams);
        RestTemplate restTemplate = new RestTemplate();
        try {
            log.debug("Request [POST] {}", url);
            log.info("## Request header ==> {}", JsonUtil.generateClassToJson(headers));
            log.info("## Request body ==> {}", JsonUtil.generateClassToJson(bodyParams));
            ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, new HttpEntity<>(bodyParams, headers), responseType);
            log.info("## Response ==> {}", JsonUtil.generateClassToJson(response));
            return (ApiResponse<T>) response.getBody();
        } catch (RestClientResponseException e) {
            log.error("## Response ==> {}", JsonUtil.generateClassToJson(e.getResponseBodyAsString()));
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }



    private HttpHeaders makeHeader(Map<String, String> headerParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (headerParams == null) {
            headerParams = new HashMap<>();
        }
        headerParams.put("x-host", GATEWAY_IDC_HOST);
        headerParams.put("x-protocol", GATEWAY_IDC_PROTOCOL);

        for (String key : headerParams.keySet()) {
            headers.add(key, headerParams.get(key));
        }

        return headers;
    }

}
