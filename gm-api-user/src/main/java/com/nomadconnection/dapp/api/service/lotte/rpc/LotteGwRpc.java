package com.nomadconnection.dapp.api.service.lotte.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.gateway.ApiResponse;
import com.nomadconnection.dapp.api.dto.lotte.DataPart1000;
import com.nomadconnection.dapp.api.dto.lotte.DataPart1100;
import com.nomadconnection.dapp.api.dto.lotte.DataPart1200;
import com.nomadconnection.dapp.api.dto.lotte.enums.LotteGwApiType;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.api.service.lotte.LotteCommonService;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteGwRpc extends LotteBaseRpc {

	@Value("${gateway.idc.domain}")
	private String GATEWAY_IDC_URL;

	@Value("${gateway.lotte.uri.1000}")
	private String GATEWAY_LOTTE_URI_1000;

	@Value("${gateway.lotte.uri.1100}")
	private String GATEWAY_LOTTE_URI_1100;

	@Value("${gateway.lotte.uri.1200}")
	private String GATEWAY_LOTTE_URI_1200;

	@Value("${gateway.lotte.uri.image-transfer}")
	private String GATEWAY_LOTTE_URI_IMAGE_TRANSFER;

	private final LotteCommonService commonService;

	public DataPart1000 request1000(DataPart1000 request, Long idxUser) {
		commonService.saveGwTran(request, idxUser);

		ApiResponse<DataPart1000> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_LOTTE_URI_1000, HttpMethod.POST,
				null, request, ApiResponse.class, LotteGwApiType.LT1000);

		if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
			throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_LOTTE_1000, "gateway error");
		}

		ObjectMapper mapper = new ObjectMapper();
		DataPart1000 responseData = mapper.convertValue(response.getData(), DataPart1000.class);
		commonService.saveGwTran(responseData, idxUser);

		if (!responseData.getResponseCode().equals(Const.API_LOTTE_RESULT_SUCCESS)) {
			throw new SystemException(ErrorCode.External.REJECTED_LOTTE_1000, responseData.getResponseCode() + "/" + responseData.getSpare());
		}

		return responseData;
	}

	public DataPart1100 request1100(DataPart1100 request, Long idxUser) {
		commonService.saveGwTran(request, idxUser);

		ApiResponse<DataPart1100> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_LOTTE_URI_1100, HttpMethod.POST,
				null, request, ApiResponse.class, LotteGwApiType.LT1100);

		if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
			throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_LOTTE_1100, "gateway error");
		}

		ObjectMapper mapper = new ObjectMapper();
		DataPart1100 responseData = mapper.convertValue(response.getData(), DataPart1100.class);
		commonService.saveGwTran(responseData, idxUser);

		if (!responseData.getResponseCode().equals(Const.API_LOTTE_RESULT_SUCCESS)) {
			throw new SystemException(ErrorCode.External.REJECTED_LOTTE_1100, responseData.getResponseCode() + "/" + responseData.getSpare());
		}

		if (!responseData.getRcpEndYn().equals(Const.API_LOTTE_RESULT_SUCCESS)) {
			throw new SystemException(ErrorCode.External.REJECTED_LOTTE_1200, responseData.getRcpEndYn() + "/" + responseData.getRcpMsg());
		}

		return responseData;
	}

	public DataPart1200 request1200(DataPart1200 request, Long idxUser) {
		commonService.saveGwTran(request, idxUser);

		ApiResponse<DataPart1200> response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_LOTTE_URI_1200, HttpMethod.POST,
				null, request, ApiResponse.class, LotteGwApiType.LT1200);

		if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
			throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_LOTTE_1200, "gateway error");
		}

		ObjectMapper mapper = new ObjectMapper();
		DataPart1200 responseData = mapper.convertValue(response.getData(), DataPart1200.class);
		commonService.saveGwTran(responseData, idxUser);

		if (!responseData.getResponseCode().equals(Const.API_LOTTE_RESULT_SUCCESS)) {
			throw new SystemException(ErrorCode.External.REJECTED_LOTTE_1200, responseData.getResponseCode() + "/" + responseData.getSpare());
		}

		if (!responseData.getReceiptYn().equals(Const.API_LOTTE_RESULT_SUCCESS)) {
			throw new SystemException(ErrorCode.External.REJECTED_LOTTE_1200, responseData.getReceiptYn() + "/" + responseData.getMessage());
		}

		return responseData;
	}

	public void requestImageTransfer(String licenseNo, Long idxUser) {
		commonService.saveGwTran(null, idxUser);

		ApiResponse response = requestGateWayByJson(GATEWAY_IDC_URL + GATEWAY_LOTTE_URI_IMAGE_TRANSFER + "/" + licenseNo
				, HttpMethod.POST, null, null, ApiResponse.class, LotteGwApiType.IMAGE_TRANSFER);

		if (!Const.API_GW_RESULT_SUCCESS.equals(response.getResult().getCode())) {
			throw new SystemException(ErrorCode.External.EXTERNAL_ERROR_LOTTE_BPR_TRANSFER, "gateway error");
		}
	}
}
