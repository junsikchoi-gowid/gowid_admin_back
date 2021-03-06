package com.nomadconnection.dapp.api.service.shinhan.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.dto.shinhan.*;
import com.nomadconnection.dapp.api.dto.shinhan.enums.ShinhanGwApiType;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.api.service.shinhan.CommonService;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShinhanGwRpc extends BaseRpc {

    @Value("${gateway.idc.shinhan}")
    private String GATEWAY_IDC_URL;

    @Value("${gateway.shinhan.uri.1200}")
    private String GATEWAY_SHINHAN_URI_1200;

    @Value("${gateway.shinhan.uri.3000}")
    private String GATEWAY_SHINHAN_URI_3000;

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

    @Value("${gateway.shinhan.uri.1710}")
    private String GATEWAY_SHINHAN_URI_1710;

    @Value("${gateway.shinhan.uri.1800}")
    private String GATEWAY_SHINHAN_URI_1800;

    @Value("${gateway.shinhan.uri.bpr-transfer}")
    private String GATEWAY_SHINHAN_URI_BPR_TRANSFER;

    @Value("${gateway.shinhan.uri.bpr-single-transfer}")
    private String GATEWAY_SHINHAN_URI_BPR_SINGLE_TRANSFER;

    private final CommonService issCommonService;


    public DataPart1200 request1200(DataPart1200 request, Long idxUser) {
        issCommonService.saveGwTran(request, idxUser);

        ApiResponse<DataPart1200> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_1200, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1200);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1200, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1200 responseData = mapper.convertValue(response.getData(), DataPart1200.class);
        issCommonService.saveGwTran(responseData, idxUser);

        return responseData;
    }

    public DataPart3000 request3000(DataPart3000 request, Long idxUser) {
        issCommonService.saveGwTran(request, idxUser);

        ApiResponse<DataPart3000> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_3000, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH3000);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_3000, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart3000 responseData = mapper.convertValue(response.getData(), DataPart3000.class);
        issCommonService.saveGwTran(responseData, idxUser);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new SystemException(ErrorCode.External.REJECTED_SHINHAN_3000, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public void requestBprTransfer(BprTransferReq request, String licenseNo, Long idxUser, CardType cardType) {

        issCommonService.saveGwTran(request, idxUser);

        ApiResponse<BprTransferRes> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_BPR_TRANSFER + "/" + licenseNo + "/" + cardType
                , HttpMethod.POST, null, request, ApiResponse.class, ShinhanGwApiType.BPR_TRANSFER);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_BPR_TRANSFER, "gateway error");
        }
    }

    public void requestBprSingleTransfer(BprTransferReq request, String licenseNo, Long idxUser, int fileType) {
        issCommonService.saveGwTran(request, idxUser);
        ApiResponse<BprTransferRes> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_BPR_SINGLE_TRANSFER + "/" + licenseNo + "/" + fileType
            , HttpMethod.POST, null, request, ApiResponse.class, ShinhanGwApiType.BPR_TRANSFER);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_BPR_TRANSFER, "gateway error");
        }
    }

    public DataPart1510 request1510(DataPart1510 request, Long idxUser) {
        issCommonService.saveGwTran(request, idxUser);

        ApiResponse<DataPart1510> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_1510, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1510);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1510, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1510 responseData = mapper.convertValue(response.getData(), DataPart1510.class);
        issCommonService.saveGwTran(responseData, idxUser);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new SystemException(ErrorCode.External.REJECTED_SHINHAN_1510, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public DataPart1520 request1520(DataPart1520 request, Long idxUser) {
        issCommonService.saveGwTran(request, idxUser);

        ApiResponse<DataPart1520> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_1520, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1520);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1520, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1520 responseData = mapper.convertValue(response.getData(), DataPart1520.class);
        issCommonService.saveGwTran(responseData, idxUser);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new SystemException(ErrorCode.External.REJECTED_SHINHAN_1520, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public DataPart1530 request1530(DataPart1530 request, Long idxUser) {
        issCommonService.saveGwTran(request, idxUser);

        ApiResponse<DataPart1530> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_1530, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1530);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1530, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1530 responseData = mapper.convertValue(response.getData(), DataPart1530.class);
        issCommonService.saveGwTran(responseData, idxUser);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new SystemException(ErrorCode.External.REJECTED_SHINHAN_1530, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public DataPart1000 request1000(DataPart1000 request, Long idxUser) {
        issCommonService.saveGwTran(request, idxUser);

        ApiResponse<DataPart1000> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_1000, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1000);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1000, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1000 responseData = mapper.convertValue(response.getData(), DataPart1000.class);
        issCommonService.saveGwTran(responseData, idxUser);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS) && !responseData.getC009().equals(Const.API_SHINHAN_RESULT_1000_1400_SUCCESS_CODE)) {
            throw new SystemException(ErrorCode.External.REJECTED_SHINHAN_1000, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public DataPart1400 request1400(DataPart1400 request, Long idxUser) {
        issCommonService.saveGwTran(request, idxUser);

        ApiResponse<DataPart1400> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_1400, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1400);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1400, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1400 responseData = mapper.convertValue(response.getData(), DataPart1400.class);
        issCommonService.saveGwTran(responseData, idxUser);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS) && !responseData.getC009().equals(Const.API_SHINHAN_RESULT_1000_1400_SUCCESS_CODE)) {
            throw new SystemException(ErrorCode.External.REJECTED_SHINHAN_1400, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public DataPart1100 request1100(DataPart1100 request, Long idxUser) {
        issCommonService.saveGwTran(request, idxUser);

        ApiResponse<DataPart1100> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_1100, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1100);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1100, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1100 responseData = mapper.convertValue(response.getData(), DataPart1100.class);
        issCommonService.saveGwTran(responseData, idxUser);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new SystemException(ErrorCode.External.REJECTED_SHINHAN_1100, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

    public DataPart1700 request1700(DataPart1700 request, Long idxUser) {
        issCommonService.saveGwTran(request, idxUser);

        ApiResponse<DataPart1700> responseRpc = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_1700, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1700);

        if (!Const.API_GW_RESULT_SUCCESS.equals(responseRpc.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1700, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1700 response1700 = mapper.convertValue(responseRpc.getData(), DataPart1700.class);
        issCommonService.saveGwTran(response1700, idxUser);

        if (!response1700.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new SystemException(ErrorCode.External.REJECTED_SHINHAN_1700, response1700.getC009() + "/" + response1700.getC013());
        }

        return response1700;

    }

    public DataPart1710 request1710(DataPart1710 request, Long idxUser) {
        issCommonService.saveGwTran(request, idxUser);

        ApiResponse<DataPart1710> responseRpc = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_1710, HttpMethod.POST,
            null, request, ApiResponse.class, ShinhanGwApiType.SH1710);

        if (!Const.API_GW_RESULT_SUCCESS.equals(responseRpc.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1710, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1710 response1710 = mapper.convertValue(responseRpc.getData(), DataPart1710.class);
        issCommonService.saveGwTran(response1710, idxUser);

        return response1710;
    }

    public DataPart1800 request1800(DataPart1800 request, Long idxUser) {
        issCommonService.saveGwTran(request, idxUser);

        ApiResponse<DataPart1800> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_SHINHAN_URI_1800, HttpMethod.POST,
                null, request, ApiResponse.class, ShinhanGwApiType.SH1800);

        if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
            throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_SHINHAN_1800, "gateway error");
        }

        ObjectMapper mapper = new ObjectMapper();
        DataPart1800 responseData = mapper.convertValue(response.getData(), DataPart1800.class);
        issCommonService.saveGwTran(responseData, idxUser);

        if (!responseData.getC009().equals(Const.API_SHINHAN_RESULT_SUCCESS)) {
            throw new SystemException(ErrorCode.External.REJECTED_SHINHAN_1800, responseData.getC009() + "/" + responseData.getC013());
        }

        return responseData;
    }

}
