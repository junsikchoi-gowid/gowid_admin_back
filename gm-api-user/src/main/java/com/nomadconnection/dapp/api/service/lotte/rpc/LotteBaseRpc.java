package com.nomadconnection.dapp.api.service.lotte.rpc;

import com.nomadconnection.dapp.api.dto.lotte.enums.LotteGwApiType;
import com.nomadconnection.dapp.api.exception.BusinessException;
import com.nomadconnection.dapp.api.util.JsonUtil;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class LotteBaseRpc {

	protected <T> T requestGateWayByJson(String gatewayUrl, HttpMethod httpMethod,
										 Map<String, String> headerParams, Object bodyParams,
										 Class<T> responseType,
										 LotteGwApiType lotteGwApiType) {
		HttpHeaders headers = makeHeader(headerParams);
		RestTemplate restTemplate = new RestTemplate();
		((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(60000);
		((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(600000);

		try {
			log.debug("Request [POST] {}", gatewayUrl);
			log.debug("## Request header ==> {}", JsonUtil.generateClassToJson(headers));
			log.debug("## Request body ==> {}", JsonUtil.generateClassToJson(bodyParams));
			ResponseEntity<T> response = restTemplate.exchange(gatewayUrl, httpMethod, new HttpEntity<>(bodyParams, headers), responseType);
			log.info("## Response ==> {}", JsonUtil.generateClassToJson(response));
			return response.getBody();

		} catch (RestClientResponseException e) {

			try {
				log.error("## Response ==> {}", JsonUtil.generateClassToJson(e.getResponseBodyAsString()));
			} catch (IOException ioException) {
				responseRpcExternalError(lotteGwApiType, e);
			}
			responseRpcExternalError(lotteGwApiType, e);

		} catch (Exception e) {
			log.error("([ LotteBaseRpc.requestGateWayByJson ]) $exception='{} => {}'", e.getMessage(), e);
			responseRpcInternalError(lotteGwApiType, e);
		}
		return null;
	}

	private HttpHeaders makeHeader(Map<String, String> headerParams) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (headerParams != null) {
			for (String key : headerParams.keySet()) {
				headers.add(key, headerParams.get(key));
			}
		}
		return headers;
	}

	private void responseRpcExternalError(LotteGwApiType lotteGwApiType, Exception e) {
		log.error("error ====== {}({})", lotteGwApiType.getName(), lotteGwApiType.getProtocolCode());

		if (LotteGwApiType.LT1000.getProtocolCode().equals(lotteGwApiType.getProtocolCode())) {
			throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_LOTTE_1000, e.getMessage());
		}
		if (LotteGwApiType.LT1100.getProtocolCode().equals(lotteGwApiType.getProtocolCode())) {
			throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_LOTTE_1100, e.getMessage());
		}
		if (LotteGwApiType.LT1200.getProtocolCode().equals(lotteGwApiType.getProtocolCode())) {
			throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_LOTTE_1200, e.getMessage());
		}

		throw new BusinessException(ErrorCode.External.EXTERNAL_ERROR_GW, "[" + lotteGwApiType.getName() + "] " + e.getMessage());
	}

	private void responseRpcInternalError(LotteGwApiType lotteGwApiType, Exception e) {
		log.error("error ====== {}({})", lotteGwApiType.getName(), lotteGwApiType.getProtocolCode());

		if (LotteGwApiType.LT1000.getProtocolCode().equals(lotteGwApiType.getProtocolCode())) {
			throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_LOTTE_1000, e.getMessage());
		}
		if (LotteGwApiType.LT1100.getProtocolCode().equals(lotteGwApiType.getProtocolCode())) {
			throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_LOTTE_1100, e.getMessage());
		}
		if (LotteGwApiType.LT1200.getProtocolCode().equals(lotteGwApiType.getProtocolCode())) {
			throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_LOTTE_1200, e.getMessage());
		}

		throw new BusinessException(ErrorCode.External.INTERNAL_ERROR_GW, "[" + lotteGwApiType.getName() + "] " + e.getMessage());
	}
}
