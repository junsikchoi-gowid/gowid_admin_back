package com.nomadconnection.dapp.api.service.notification;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.api.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SlackNotiRpc {

    private final ObjectMapper objectMapper;

    public void send(Object object, String slackWebHookUrl) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String payload = writeValueAsString(object);
            log.debug("## Request [POST] {}", slackWebHookUrl);
            log.info("## Request body (payload) = [{}]", payload);
            restTemplate.postForEntity(slackWebHookUrl, payload, String.class);
        } catch (RestClientResponseException e) {
            try {
                log.error("## Response ==> {}", JsonUtil.generateClassToJson(e.getResponseBodyAsString()));
                log.error(e.getMessage(), e);
            } catch (IOException ioException) {
                log.error(e.getMessage(), e);
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }

    }

    private String writeValueAsString(Object obj) throws JsonProcessingException {
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        return objectMapper.writeValueAsString(obj);
    }

}
