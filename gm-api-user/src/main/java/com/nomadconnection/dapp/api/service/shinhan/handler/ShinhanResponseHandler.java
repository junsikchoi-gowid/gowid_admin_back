package com.nomadconnection.dapp.api.service.shinhan.handler;

import com.nomadconnection.dapp.api.dto.shinhan.enums.ShinhanInterfaceId;
import com.nomadconnection.dapp.api.enums.ShinhanResponse;
import com.nomadconnection.dapp.api.exception.kised.KisedException;
import com.nomadconnection.dapp.api.exception.shinhan.ShinhanInternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import static com.nomadconnection.dapp.api.common.Const.API_SHINHAN_RESULT_SUCCESS;

@Slf4j
public class ShinhanResponseHandler {

	public static void handleResponse1710(String responseCode, String responseMessage) throws Exception {
		if(StringUtils.isEmpty(responseCode)){
			log.error("No response code from Shinhan Card");
			throw ShinhanInternalException.builder()
				.code(responseCode)
				.desc(ShinhanResponse.findByInterfaceIdAndResponseCode(ShinhanInterfaceId.SH1710, responseCode).getResponseMessage())
				.shinhanMessage(responseMessage)
				.build();
		} else if(ShinhanResponse.SH1710_SHINHAN_INTERNAL_ERROR.getResponseCode().equals(responseCode)){
			log.error("Shinhan Card internal error");
			throw ShinhanInternalException.builder()
				.code(responseCode)
				.desc(ShinhanResponse.findByInterfaceIdAndResponseCode(ShinhanInterfaceId.SH1710, responseCode).getResponseMessage())
				.shinhanMessage(responseMessage)
				.build();
		} else if(!API_SHINHAN_RESULT_SUCCESS.equals(responseCode)){
			log.error("Failed to verify projectId during 1710");
			throw KisedException.builder()
				.code(responseCode)
				.desc(ShinhanResponse.findByInterfaceIdAndResponseCode(ShinhanInterfaceId.SH1710, responseCode).getResponseMessage())
				.shinhanMessage(responseMessage)
				.build();
		}
	}

}
