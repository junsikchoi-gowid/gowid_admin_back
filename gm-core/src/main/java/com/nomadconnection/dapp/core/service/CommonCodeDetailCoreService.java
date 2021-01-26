package com.nomadconnection.dapp.core.service;

import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;

@Service
@RequiredArgsConstructor
public class CommonCodeDetailCoreService {

	private final CommonCodeDetailRepository commonCodeDetailRepository;

	public CommonCodeDetail findByCodeAndCode1(CommonCodeType code, String code1) throws Exception {
		return commonCodeDetailRepository.findFirstByCode1AndCode(code1, code).orElseThrow(
			() -> new NoResultException()
		);
	}

}
