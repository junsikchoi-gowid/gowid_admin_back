package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1200Repository;
import com.nomadconnection.dapp.core.domain.shinhan.D1200;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class D1200Service {

	private final D1200Repository d1200Repository;

	public D1200 getD1200ByApplicationDateAndApplicationNum(String applicationDate, String applicationNum) {
		return d1200Repository.findFirstByD007AndD008OrderByUpdatedAtDesc(applicationDate, applicationNum).orElseThrow(
			() -> new BadRequestException(ErrorCode.Api.NOT_FOUND,
				"not found d1200 of applicationDate[" + applicationDate + "], applicationNum[" + applicationNum + "]")
		);
	}
}
