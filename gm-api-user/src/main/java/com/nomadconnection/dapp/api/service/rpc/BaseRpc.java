package com.nomadconnection.dapp.api.service.rpc;

import com.nomadconnection.dapp.api.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;


@Slf4j
@Component
public class BaseRpc {

    @Value("${gateway.idc.domain}")
    private String GATEWAY_IDC_DOMAIN;

    protected <T> T requestGateway(String url,
                                   HttpMethod httpMethod,
                                   Map<String, String> headerParams,
                                   Object bodyParams,
                                   Class<T> responseType) throws IOException {

        headerParams.put("x-host", GATEWAY_IDC_DOMAIN);
        HttpHeaders headers = this.makeHeader(headerParams);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
//        try {
        ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, new HttpEntity<>(bodyParams, headers), responseType);
        log.info("## Response ==> {}", JsonUtil.generateClassToJson(response));
        return response.getBody();
//        } catch (RestClientResponseException e) {
//            log.error("## Response ==> {}", JsonUtil.generateClassToJson(e.getResponseBodyAsString()));
//            throw e;
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw e;
//        }
    }

    private HttpHeaders makeHeader(Map<String, String> headerParams) {
        HttpHeaders headers = new HttpHeaders();

        if (headerParams != null) {
            for (String key : headerParams.keySet()) {
                headers.add(key, headerParams.get(key));
            }
        }

        return headers;
    }


}
