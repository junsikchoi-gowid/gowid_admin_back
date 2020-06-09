package com.nomadconnection.dapp.api.service.rpc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.*;
import com.nomadconnection.dapp.api.dto.shinhan.gateway.response.ApiResponse;
import com.nomadconnection.dapp.api.util.JsonUtil;
import com.nomadconnection.dapp.api.util.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ShinhanGwRpc extends BaseRpc {

    @Value("${gateway.aws.domain}")
    private String GATEWAY_AWS_DOMAIN;

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

    @Value("${gateway.idc.host}")
    private String GATEWAY_IDC_HOST;

    @Value("${gateway.idc.protocol}")
    private String GATEWAY_IDC_PROTOCOL;

//    public DataPart1200 request1200(DataPart1200 requestRpc) {
//
//        ApiResponse<DataPart1200> responseRpc;
//        try {
//            responseRpc = requestGateway(GATEWAY_AWS_DOMAIN + GATEWAY_SHINHAN_URI_1200,
//                    HttpMethod.POST,
//                    null,
//                    requestRpc,
//                    ApiResponse<DataPart1200>);
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200, e.getMessage());
//
//        } catch (RestClientResponseException e) {
//            log.error("## Response ==> {}", e.getResponseBodyAsString());
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200, e.getMessage());
//        }
//
//        if (responseRpc == null || responseRpc.getResult().getCode() != Const.API_GW_RESULT_SUCCESS) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200);
//        }
//
//        DataPart1200 response1200 = responseRpc.getData();
//        if (!response1200.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
//            throw new BusinessException(ErrorCode.External.REJECTED_SHINHAN_1200, response1200.getC009() + "/" + response1200.getC013());
//        }
//
//        return response1200;
//
//    }

    // todo : 예
    public DataPart1200 request1200(DataPart1200 requestRpc, HttpMethod httpMethod) {
        ApiResponse responseRpc = null;
        String url = GATEWAY_AWS_DOMAIN + GATEWAY_SHINHAN_URI_1200;
        HttpHeaders headers = makeHeader();
        log.debug("Request [{}}] {}", httpMethod.name(), url);
        log.info("## Request header ==> {}", LoggingUtils.getPrettyJsonString(headers));
        log.info("## Request body ==> {}", LoggingUtils.getPrettyJsonString(requestRpc));

        RestTemplate restTemplate = new RestTemplate();

        try {
            responseRpc = restTemplate.postForObject(url, new HttpEntity<>(requestRpc, headers), ApiResponse.class);
            log.debug("## Response ==> {}", LoggingUtils.getPrettyJsonString(responseRpc));
        } catch (RestClientResponseException e) {
            log.error("## Fail to request user auth code!");
            log.error("## Response ==> {}", e.getResponseBodyAsString());
            responseRpc = getApiErrorResponse(responseRpc, e);
        } catch (Exception e) {
            log.error("## Fail to request user auth code!");
            log.error(e.getMessage(), e);
        }

        if (responseRpc == null) {
            return null;
        }

        return (DataPart1200)(responseRpc.getData());
    }

    private ApiResponse getApiErrorResponse(ApiResponse responseRpc, RestClientResponseException e) {
        try {
            responseRpc = JsonUtil.generateJsonToClass(e.getResponseBodyAsString(), new TypeReference<ApiResponse>() {
            });
        } catch (IOException ioe) {
            log.error("## Fail to request user auth code!");
            log.error(ioe.getMessage(), ioe);
            ioe.printStackTrace();
        }
        return responseRpc;
    }

    private HttpHeaders makeHeader() {
        return makeHeader();
    }
    private HttpHeaders makeHeader(Map<String, String> headerParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (headerParams == null) {
            headerParams = new HashMap<>();
        }
        headerParams.put("x-host", GATEWAY_IDC_HOST);
        headerParams.put("x-protocol", GATEWAY_IDC_PROTOCOL);

        for (String key : headerParams.keySet()) {
            headers.add(key, headerParams.get(key));
        }

        return headers;
    }

    public void request1510(DataPart1510 requestRpc) {

//        ApiResponse<?> responseRpc;
//        try {
//            responseRpc = requestGateway(GATEWAY_AWS_DOMAIN + GATEWAY_SHINHAN_URI_1510,
//                    HttpMethod.POST,
//                    null,
//                    requestRpc,
//                    ApiResponse.class);
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1510, e.getMessage());
//        } catch (RestClientResponseException e) {
//            log.error("## Response ==> {}", e.getResponseBodyAsString());
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1510, e.getMessage());
//        }
//
//        if (responseRpc == null || responseRpc.getResult().getCode() != Const.API_GW_RESULT_SUCCESS) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1510);
//        }
//
//        DataPart1510 response1510 = (DataPart1510) responseRpc.getData();
//        if (!response1510.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1510, response1510.getC009() + "/" + response1510.getC013());
//        }

    }

    public void request1520(DataPart1520 requestRpc) {

//        ApiResponse<?> responseRpc;
//        try {
//            responseRpc = requestGateway(GATEWAY_AWS_DOMAIN + GATEWAY_SHINHAN_URI_1520,
//                    HttpMethod.POST,
//                    null,
//                    requestRpc,
//                    ApiResponse.class);
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1520, e.getMessage());
//        } catch (RestClientResponseException e) {
//            log.error("## Response ==> {}", e.getResponseBodyAsString());
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1520, e.getMessage());
//        }
//
//        if (responseRpc == null || responseRpc.getResult().getCode() != Const.API_GW_RESULT_SUCCESS) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1520);
//        }
//
//        DataPart1520 response1520 = (DataPart1520) responseRpc.getData();
//        if (!response1520.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1520, response1520.getC009() + "/" + response1520.getC013());
//        }

    }

    public void request1530(DataPart1530 requestRpc) {

//        ApiResponse<?> responseRpc;
//        try {
//            responseRpc = requestGateway(GATEWAY_AWS_DOMAIN + GATEWAY_SHINHAN_URI_1530,
//                    HttpMethod.POST,
//                    null,
//                    requestRpc,
//                    ApiResponse.class);
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1530, e.getMessage());
//        } catch (RestClientResponseException e) {
//            log.error("## Response ==> {}", e.getResponseBodyAsString());
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1530, e.getMessage());
//        }
//
//        if (responseRpc == null || responseRpc.getResult().getCode() != Const.API_GW_RESULT_SUCCESS) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1530);
//        }
//
//        DataPart1530 response1530 = (DataPart1530) responseRpc.getData();
//        if (!response1530.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1530, response1530.getC009() + "/" + response1530.getC013());
//        }
    }

    public void request1000(DataPart1000 requestRpc) {

//        ApiResponse<?> responseRpc;
//        try {
//            responseRpc = requestGateway(GATEWAY_AWS_DOMAIN + GATEWAY_SHINHAN_URI_1000,
//                    HttpMethod.POST,
//                    null,
//                    requestRpc,
//                    ApiResponse.class);
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1000, e.getMessage());
//        } catch (RestClientResponseException e) {
//            log.error("## Response ==> {}", e.getResponseBodyAsString());
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1000, e.getMessage());
//        }
//
//        if (responseRpc == null || responseRpc.getResult().getCode() != Const.API_GW_RESULT_SUCCESS) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1000);
//        }
//
//        DataPart1000 response1000 = (DataPart1000) responseRpc.getData();
//        if (!response1000.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1000, response1000.getC009() + "/" + response1000.getC013());
//        }
    }

    public void request1400(DataPart1400 requestRpc) {

//        ApiResponse<?> responseRpc;
//        try {
//            responseRpc = requestGateway(GATEWAY_AWS_DOMAIN + GATEWAY_SHINHAN_URI_1400,
//                    HttpMethod.POST,
//                    null,
//                    requestRpc,
//                    ApiResponse.class);
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1400, e.getMessage());
//        } catch (RestClientResponseException e) {
//            log.error("## Response ==> {}", e.getResponseBodyAsString());
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1400, e.getMessage());
//        }
//
//        if (responseRpc == null || responseRpc.getResult().getCode() != Const.API_GW_RESULT_SUCCESS) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1400);
//        }
//
//        DataPart1400 response1400 = (DataPart1400) responseRpc.getData();
//        if (!response1400.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1400, response1400.getC009() + "/" + response1400.getC013());
//        }
    }

    public void request1100(DataPart1100 requestRpc) {

//        ApiResponse<?> responseRpc;
//        try {
//            responseRpc = requestGateway(GATEWAY_AWS_DOMAIN + GATEWAY_SHINHAN_URI_1100,
//                    HttpMethod.POST,
//                    null,
//                    requestRpc,
//                    ApiResponse.class);
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1100, e.getMessage());
//        } catch (RestClientResponseException e) {
//            log.error("## Response ==> {}", e.getResponseBodyAsString());
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1100, e.getMessage());
//        }
//
//        if (responseRpc == null || responseRpc.getResult().getCode() != Const.API_GW_RESULT_SUCCESS) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1100);
//        }
//
//        DataPart1100 response1100 = (DataPart1100) responseRpc.getData();
//        if (!response1100.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1100, response1100.getC009() + "/" + response1100.getC013());
//        }
    }


}
