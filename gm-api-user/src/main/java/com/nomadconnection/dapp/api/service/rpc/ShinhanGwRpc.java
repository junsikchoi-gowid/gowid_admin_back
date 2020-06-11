package com.nomadconnection.dapp.api.service.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.*;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.response.ApiResponse;
import com.nomadconnection.dapp.api.exception.BusinessException;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShinhanGwRpc extends BaseRpc {

    @Value("${gateway.aws.domain}")
    private String GATEWAY_AWS_URL;

    @Value("${gateway.shinhan.uri.1200}")
    private String GATEWAY_SHINHAN_URI_1200;

    @Value("${gateway.shinhan.uri.1510}")
    private String GATEWAY_SHINHAN_URI_1510;

    @Value("${gateway.shinhan.uri.1520}")
    private String GATEWAY_SHINHAN_URI_1520;

    @Value("${gateway.shinhan.uri.1530}")
    private String GATEWAY_SHINHAN_URI_1530;

    @Value("${gateway.shinhan.uri.1000}")
    private String GATEWAY_SHINHAN_URI_1000;

    @Value("${gateway.shinhan.uri.1400}")
    private String GATEWAY_SHINHAN_URI_1400;

    @Value("${gateway.shinhan.uri.1100}")
    private String GATEWAY_SHINHAN_URI_1100;

    @Value("${gateway.shinhan.uri.1700}")
    private String GATEWAY_SHINHAN_URI_1700;

    public DataPart1200 request1200(DataPart1200 request) {

        ApiResponse<DataPart1200> response = requestGateWayByJson(GATEWAY_AWS_URL + GATEWAY_SHINHAN_URI_1200, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1200);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1200 responseData = mapper.convertValue(response.getData(), DataPart1200.class);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new BusinessException(ErrorCode.External.REJECTED_SHINHAN_1200, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;

    }

    public DataPart1510 request1510(DataPart1510 request) {

        ApiResponse<DataPart1510> response = requestGateWayByJson(GATEWAY_AWS_URL + GATEWAY_SHINHAN_URI_1510, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1510);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1510, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1510 responseData = mapper.convertValue(response.getData(), DataPart1510.class);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new BusinessException(ErrorCode.External.REJECTED_SHINHAN_1510, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public DataPart1520 request1520(DataPart1520 request) {

        ApiResponse<DataPart1520> response = requestGateWayByJson(GATEWAY_AWS_URL + GATEWAY_SHINHAN_URI_1520, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1520);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1520, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1520 responseData = mapper.convertValue(response.getData(), DataPart1520.class);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new BusinessException(ErrorCode.External.REJECTED_SHINHAN_1520, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public DataPart1530 request1530(DataPart1530 request) {

        ApiResponse<DataPart1530> response = requestGateWayByJson(GATEWAY_AWS_URL + GATEWAY_SHINHAN_URI_1530, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1530);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1530, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1530 responseData = mapper.convertValue(response.getData(), DataPart1530.class);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new BusinessException(ErrorCode.External.REJECTED_SHINHAN_1530, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public DataPart1000 request1000(DataPart1000 request) {

        ApiResponse<DataPart1000> response = requestGateWayByJson(GATEWAY_AWS_URL + GATEWAY_SHINHAN_URI_1000, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1000);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1000, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1000 responseData = mapper.convertValue(response.getData(), DataPart1000.class);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new BusinessException(ErrorCode.External.REJECTED_SHINHAN_1000, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public DataPart1400 request1400(DataPart1400 request) {

        ApiResponse<DataPart1400> response = requestGateWayByJson(GATEWAY_AWS_URL + GATEWAY_SHINHAN_URI_1400, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1400);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1400, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1400 responseData = mapper.convertValue(response.getData(), DataPart1400.class);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new BusinessException(ErrorCode.External.REJECTED_SHINHAN_1400, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public DataPart1100 request1100(DataPart1100 request) {

        ApiResponse<DataPart1100> response = requestGateWayByJson(GATEWAY_AWS_URL + GATEWAY_SHINHAN_URI_1100, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1100);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1100, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1100 responseData = mapper.convertValue(response.getData(), DataPart1100.class);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new BusinessException(ErrorCode.External.REJECTED_SHINHAN_1100, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public DataPart1700 request1700(DataPart1700 request) {

        ApiResponse<DataPart1700> responseRpc = requestGateWayByJson(GATEWAY_AWS_URL + GATEWAY_SHINHAN_URI_1700, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1700);

        if (!Const.API_GW_RESULT_SUCCESS.equals(responseRpc.getResult().getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1700, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1700 response1700 = mapper.convertValue(responseRpc.getData(), DataPart1700.class);

        if (!response1700.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new BusinessException(ErrorCode.External.REJECTED_SHINHAN_1700, response1700.getC009() + "/" + response1700.getC013());
        }

        return response1700;

    }

}
