package com.nomadconnection.dapp.api.service.rpc;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.DataPart_1200;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ShinhanGwRpc extends BaseRpc {

    @Value("${gateway.domain: http://loclhost:8090}")
    private String GATEWAY_DOMAIN;

    @Value("${gateway.shinhan.uri.1200: /shinhan/1200}")
    private String GATEWAY_SHINHAN_URI_1200;

    public void request_1200(DataPart_1200 requestRpc) {

        ApiResponse<?> responseRpc = null;
        try {
            responseRpc = requestGateway(GATEWAY_DOMAIN + GATEWAY_SHINHAN_URI_1200,
                    HttpMethod.POST,
                    null,
                    requestRpc,
                    ApiResponse.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        if (responseRpc == null) {
            // todo : 실패처리
        }

        if (responseRpc.getResult().getCode() != Const.API_GW_RESULT_SUCCESS) {
            // todo : 실패 처리
        }

        DataPart_1200 response1200 = (DataPart_1200) responseRpc.getData();
        if (!response1200.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            // todo : 실패 처리
        }

    }
}
