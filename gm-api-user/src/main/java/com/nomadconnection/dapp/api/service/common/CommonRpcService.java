package com.nomadconnection.dapp.api.service.common;

import com.nomadconnection.dapp.api.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class CommonRpcService {

    public <T> ResponseEntity<T> requestApi(String url, HttpMethod httpMethod, Object bodyParams, Class<T> responseType, HttpHeaders headers, RestTemplate restTemplate) throws IOException {
        log.debug("## Request [{}] {}", httpMethod, url);
        log.debug("## Request header ==> {}", JsonUtil.generateClassToJson(headers));
        // TODO hyuntak 리팩토링 대상
        log.debug("## Request body ==> {}", ObjectUtils.isEmpty(bodyParams) ? null : bodyParams.toString());

        ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, new HttpEntity<>(bodyParams, headers), responseType);
        log.info("## Response ==> {}", JsonUtil.generateClassToJson(response));

        return response;
    }

    public HttpHeaders makeHeader(Map<String, String> headerParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (headerParams != null) {
            for (String key : headerParams.keySet()) {
                headers.add(key, headerParams.get(key));
            }
        }
        return headers;
    }

}
