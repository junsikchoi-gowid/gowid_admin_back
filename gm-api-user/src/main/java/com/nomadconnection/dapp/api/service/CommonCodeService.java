package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.CommonCodeDto;
import com.nomadconnection.dapp.api.exception.CodeNotFoundException;
import com.nomadconnection.dapp.core.domain.common.CommonCode;
import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeRepository;
import com.nomadconnection.dapp.core.security.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonCodeService {

	private final CommonCodeRepository commonCodeRepository;

	public CommonCodeDto findByCode(CommonCodeType code){
		CommonCode commonCode = commonCodeRepository.findAllByCode(code)
			.orElseThrow(() -> new CodeNotFoundException(code));

		return CommonCodeDto.builder().code(commonCode.code()).desc(commonCode.codeDesc()).build();
	}
}