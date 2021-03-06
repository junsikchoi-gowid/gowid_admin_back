package com.nomadconnection.dapp.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.KcbDto;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.ServerError;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KcbService {

    private final UserRepository repoUser;

    private final WebClient gwShinhanClient;
    private final WebClient gwLotteClient;
    private final ObjectMapper objectMapper;

    public KcbDto.Response authenticationSms(Long idx_user, KcbDto.Authentication dto) throws IOException {
        User user = findUser(idx_user);
        String url = UriComponentsBuilder.newInstance()
                .path("/kcb/sms")
                .build()
                .toUriString();

        String deviceId = dto.getDeviceId();
        if (!StringUtils.hasText(deviceId)) {
            deviceId = UUID.randomUUID().toString();
            dto.setDeviceId(deviceId);
        }
        log.info("[authenticationSms] $url({}), $dto({})", url, dto);

        ClientResponse clientResponse;
        if (CardCompany.LOTTE.equals(user.cardCompany())) {
            clientResponse = gwLotteClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromObject(dto))
                    .exchange().block();
        } else {
            clientResponse = gwShinhanClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromObject(dto))
                    .exchange().block();
        }

        KcbDto.Response response = responseDataResolver(clientResponse, KcbDto.Response.class);
        log.info("[authenticationSms] $response.status({}), $response.result({})", response.getData().getCode(), response.getData().getDesc());

        if (!response.getData().getCode().equals(Const.API_GW_OKNAME_SUCCESS)) {
            log.error("([ authenticationSms ]) $response.status({}), $response.result({})", response.getData().getCode(), response.getData().getDesc());
            throw ServerError.builder().category(ServerError.Category.KCB_SERVER_ERROR).data(response).build();
        }

        return response.setDeviceId(deviceId);
    }

    public KcbDto.Response certSms(Long idx_user, KcbDto.Cert dto) throws IOException {
        User user = findUser(idx_user);
        String url = UriComponentsBuilder.newInstance()
                .path("/kcb/cert")
                .build()
                .toUriString();

        log.info("[certSms] $url({}), $dto({})", url, dto);

        ClientResponse clientResponse;
        if (CardCompany.LOTTE.equals(user.cardCompany())) {
            clientResponse = gwLotteClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromObject(dto))
                    .exchange().block();
        } else {
            clientResponse = gwShinhanClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromObject(dto))
                    .exchange().block();
        }

        KcbDto.Response response = responseDataResolver(clientResponse, KcbDto.Response.class);
        log.info("[certSms] $response.status({}), $response.result({})", response.getData().getCode(), response.getData().getDesc());

        if (!response.getData().getCode().equals(Const.API_GW_OKNAME_SUCCESS)) {
            log.error("([ certSms ]) $response.status({}), $response.result({})", response.getData().getCode(), response.getData().getDesc());
            throw ServerError.builder().category(ServerError.Category.KCB_SERVER_ERROR).data(response).build();
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

    private User findUser(Long idx_user) {
        return repoUser.findById(idx_user).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("User")
                        .idx(idx_user)
                        .build()
        );
    }
}
