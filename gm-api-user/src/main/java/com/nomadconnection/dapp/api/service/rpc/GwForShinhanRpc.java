package com.nomadconnection.dapp.api.service.rpc;

import com.nomadconnection.dapp.api.dto.gateway.shinhan.request.DataPart_1200;
import com.nomadconnection.dapp.api.dto.gateway.shinhan.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GwForShinhanRpc extends BaseRpc {

    @Value("${gateway.domain:none}")
    private String GATEWAY_DOMAIN;

    @Value("${gateway.shinhan.uri.1200:none}")
    private String GATEWAY_SHINHAN_URI_1200;

    public ApiResponse<DataPart_1200> request_1200(DataPart_1200 request) throws IOException {

        // todo : 제네릭 타입 수정
        return requestGateway(GATEWAY_DOMAIN + GATEWAY_SHINHAN_URI_1200,
                HttpMethod.POST,
                null,
                request,
                ApiResponse.class);

    }
}
