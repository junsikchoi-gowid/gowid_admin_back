package com.nomadconnection.dapp.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.GwUploadDto;
import com.nomadconnection.dapp.api.exception.ServerError;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
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

    private final WebClient gwShinhanClient;
    private final WebClient gwLotteClient;
    private final ObjectMapper objectMapper;

    public GwUploadDto.Response upload(CardCompany cardCompany, File file, String fileCode, String licenseNo) throws IOException {
        log.info("[GwUpload] $cardCompany({}), $fileCode({}), $licenseNo({})", cardCompany.name(), fileCode, licenseNo);
        String url = UriComponentsBuilder.newInstance()
                .path("/upload/" + cardCompany.getCode())
                .build()
                .toUriString();

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("files", new FileSystemResource(file));
        params.add("licenseNo", licenseNo);
        params.add("fileType", fileCode);

        ClientResponse clientResponse;
        if (cardCompany.equals(CardCompany.LOTTE)) {
            clientResponse = gwLotteClient.post()
                    .uri(url)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(params))
                    .exchange().block();
        } else {
            clientResponse = gwShinhanClient.post()
                    .uri(url)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(params))
                    .exchange().block();
        }

        GwUploadDto.Response response = responseDataResolver(clientResponse, GwUploadDto.Response.class);
        log.info("[GwUpload] $response.status({}), $response.result({}), $response.data({})", response.getResult().getCode(), response.getResult().getDesc(), response.getData());

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            log.error("([ GwUpload ]) $response.status({}), $response.result({})", response.getResult().getCode(), response.getResult().getDesc());
            throw ServerError.builder().category(ServerError.Category.GW_UPLOAD_SERVER_ERROR).data(response).build();
        }

        return response;
    }

    public GwUploadDto.Response delete(CardCompany cardCompany, String fileName) throws IOException {
        String url = UriComponentsBuilder.newInstance()
                .path("/files/" + cardCompany.getCode() + "?files=" + fileName)
                .build()
                .toUriString();

        ClientResponse clientResponse;
        if (cardCompany.equals(CardCompany.LOTTE)) {
            clientResponse = gwLotteClient.delete()
                    .uri(url)
                    .exchange().block();
        } else {
            clientResponse = gwShinhanClient.delete()
                    .uri(url)
                    .exchange().block();
        }

        GwUploadDto.Response response = responseDataResolver(clientResponse, GwUploadDto.Response.class);
        log.info("[GwDelete] $response.status({}), $response.result({}), $response.data({})", response.getResult().getCode(), response.getResult().getDesc(), response.getData());

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode()) && !Const.API_GW_IMAGE_NOT_EXIST_ERROR_CODE.equals(response.getResult().getCode())) {
            log.error("([ GwDelete ]) $response.status({}), $response.result({})", response.getResult().getCode(), response.getResult().getDesc());
            throw ServerError.builder().category(ServerError.Category.GW_DELETE_SERVER_ERROR).data(response).build();
        }

        return response;
    }

    private <T> T responseDataResolver(ClientResponse clientResponse, Class<T> responseType) throws IOException {
        String body = clientResponse.toEntity(String.class).block().getBody();
        log.info("[responseDataResolver] $BODY({})", body);
        try {
            return objectMapper.readValue(body, responseType);
        } catch (IOException e) {
            log.error("[responseDataResolver] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage());
            throw new IOException();
        }
    }
}
