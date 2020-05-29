package com.nomadconnection.dapp.api.service.rpc;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.DataPart1200;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.DataPart1510;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.response.ApiResponse;
import com.nomadconnection.dapp.api.exception.BusinessException;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
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

    @Value("${gateway.shinhan.uri.1510: /shinhan/1510}")
    private String GATEWAY_SHINHAN_URI_1200;

    @Value("${gateway.shinhan.uri.1510: /shinhan/1510}")
    private String GATEWAY_SHINHAN_URI_1510;

    public void request1510(DataPart1510 requestRpc) {

        ApiResponse<?> responseRpc;
        try {
            responseRpc = requestGateway(GATEWAY_DOMAIN + GATEWAY_SHINHAN_URI_1510,
                    HttpMethod.POST,
                    null,
                    requestRpc,
                    ApiResponse.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1510);
        }

        if (responseRpc == null || responseRpc.getResult().getCode() != Const.API_GW_RESULT_SUCCESS) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1510);
        }

        DataPart1510 response1510 = (DataPart1510) responseRpc.getData();
        if (!response1510.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1510);
        }

    }

    public void request1200(DataPart1200 requestRpc) {

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

        if (responseRpc == null || responseRpc.getResult().getCode() != Const.API_GW_RESULT_SUCCESS) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200);
        }

        DataPart1200 response1200 = (DataPart1200) responseRpc.getData();
        if (!response1200.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200);
        }

    }


}
