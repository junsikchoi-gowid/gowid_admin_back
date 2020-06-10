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

    @Value("${gateway.idc.host}")
    private String GATEWAY_IDC_HOST;

    @Value("${gateway.idc.protocol}")
    private String GATEWAY_IDC_PROTOCOL;

    @Value("${gateway.shinhan.uri.1700}")
    private String GATEWAY_SHINHAN_URI_1700;

    public DataPart1200 request1200(DataPart1200 request) {

        ApiResponse<DataPart1200> responseRpc = requestGateWayByJson(GATEWAY_AWS_URL + GATEWAY_SHINHAN_URI_1200, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1200);

        if (!Const.API_GW_RESULT_SUCCESS.equals(responseRpc.getResult().getCode())) {
            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1200 response1200 = mapper.convertValue(responseRpc.getData(), DataPart1200.class);

        if (!response1200.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new BusinessException(ErrorCode.External.REJECTED_SHINHAN_1200, response1200.getC009() + "/" + response1200.getC013());
        }

        return response1200;

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

//    public DataPart1200 request1200(DataPart1200 requestRpc, HttpMethod httpMethod) {
//        ApiResponse<DataPart1200> responseRpc;
//        String url = GATEWAY_AWS_DOMAIN + GATEWAY_SHINHAN_URI_1200;
//        HttpHeaders headers = makeHeader();
//                                    log.debug("Request [{}}] {}", httpMethod.name(), url);
//        log.info("## Request header ==> {}", LoggingUtils.getPrettyJsonString(headers));
//        log.info("## Request body ==> {}", LoggingUtils.getPrettyJsonString(requestRpc));
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        try {
//            responseRpc = (ApiResponse<DataPart1200>)restTemplate.postForObject(url, new HttpEntity<>(requestRpc, headers), ApiResponse.class);
//            log.debug("## Response ==> {}", LoggingUtils.getPrettyJsonString(responseRpc));
//        } catch (RestClientResponseException e) {
//            log.error("## Response ==> {}", e.getResponseBodyAsString());
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200, e.getMessage());
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200, e.getMessage());
//        }
//
//        if (responseRpc == null || responseRpc.getResult().getCode() != Const.API_GW_RESULT_SUCCESS) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200, "gateway error");
//        }
//
//        DataPart1200 response1200 = (DataPart1200) responseRpc.getData();
//        if (!response1200.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
//            throw new BusinessException(ErrorCode.External.REJECTED_SHINHAN_1200, response1200.getC009() + "/" + response1200.getC013());
//        }
//
//        return response1200;
//    }

//    private HttpHeaders makeHeader() {
//        return makeHeader(null);
//    }
//    private HttpHeaders makeHeader(Map<String, String> headerParams) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        if (headerParams == null) {
//            headerParams = new HashMap<>();
//        }
//        headerParams.put("x-host", GATEWAY_IDC_HOST);
//        headerParams.put("x-protocol", GATEWAY_IDC_PROTOCOL);
//
//        for (String key : headerParams.keySet()) {
//            headers.add(key, headerParams.get(key));
//        }
//
//        return headers;
//    }

//    public DataPart1700 request1700(DataPart1700 requestRpc) {
//
//        ApiResponse<?> responseRpc;
//        try {
//            responseRpc = requestGateway(GATEWAY_AWS_DOMAIN + GATEWAY_SHINHAN_URI_1700,
//                    HttpMethod.POST,
//                    null,
//                    requestRpc,
//                    ApiResponse.class);
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1700, e.getMessage());
//        } catch (RestClientResponseException e) {
//            log.error("## Response ==> {}", e.getResponseBodyAsString());
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1700, e.getMessage());
//        }
//
//        if (responseRpc == null || responseRpc.getResult().getCode() != Const.API_GW_RESULT_SUCCESS) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1700);
//        }
//
//        DataPart1700 response1700 = (DataPart1700) responseRpc.getData();
//        if (!response1700.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
//            throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1700, response1700.getC009() + "/" + response1700.getC013());
//        }
//
//        return response1700;
//    }

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
