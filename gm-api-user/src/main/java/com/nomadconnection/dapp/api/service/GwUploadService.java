package com.nomadconnection.dapp.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.GwUploadDto;
import com.nomadconnection.dapp.api.exception.ServerError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GwUploadService {

    @Value("${gateway.idc.host}")
    private String GATEWAY_IDC_HOST;

    @Value("${gateway.idc.protocol}")
    private String GATEWAY_IDC_PROTOCOL;

    private final WebClient gwClient;
    private final ObjectMapper objectMapper;

    public GwUploadDto.Response upload(File file, String cardCode) throws IOException {
        String url = UriComponentsBuilder.newInstance()
                .path("/upload")
                .build()
                .toUriString();

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("files", file);

        ClientResponse clientResponse = gwClient.post()
                .uri(url + "/" + cardCode)
                .header("x-host", GATEWAY_IDC_HOST)
                .header("x-protocol", GATEWAY_IDC_PROTOCOL)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(params))
                .exchange().block();

        GwUploadDto.Response response = responseDataResolver(clientResponse, GwUploadDto.Response.class);
        log.info("[upload] $response.status({}), $response.result({})", response.getResult().getCode(), response.getResult().getDesc());

        if (!response.getResult().getCode().equals(Const.API_GW_RESULT_SUCCESS)) {
            log.error("([ upload ]) $response.status({}), $response.result({})", response.getResult().getCode(), response.getResult().getDesc());
            throw ServerError.builder().category(ServerError.Category.GW_UPLOAD_SERVER_ERROR).data(response).build();
        }

        return response;
    }

    private <T> T responseDataResolver(ClientResponse clientResponse, Class<T> responseType) throws IOException {
        String body = clientResponse.toEntity(String.class).block().getBody();
        log.trace("[responseDataResolver] $BODY({})", body);
        try {
            return objectMapper.readValue(body, responseType);
        } catch (IOException e) {
            log.error("[responseDataResolver] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage());
            throw new IOException();
        }
    }
}
