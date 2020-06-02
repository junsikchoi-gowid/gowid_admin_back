package com.nomadconnection.dapp.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.api.dto.GwUploadDto;
import com.nomadconnection.dapp.api.exception.ServerError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GwUploadService {

    private final WebClient gwClient;
    private final ObjectMapper objectMapper;

    public GwUploadDto.Response upload(MultipartFile file, String cardCode) throws IOException {
        String url = UriComponentsBuilder.newInstance()
                .path("/upload")
                .build()
                .toUriString();

        ClientResponse clientResponse = this.gwClient.post()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("files", file)) // TODO: cardCode
                .exchange().block();

        GwUploadDto.Response response = this.responseDataResolver(clientResponse, GwUploadDto.Response.class);
        log.info("[upload] $response.status({}), $response.result({})", response.getData().getCode(), response.getData().getDesc());

        if (!response.getData().getCode().equals("200")) {
            log.error("([ upload ]) $response.status({}), $response.result({})", response.getData().getCode(), response.getData().getDesc());
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
