package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.core.domain.User;
import com.nomadconnection.dapp.resx.config.ResourceConfig;
import com.nomadconnection.dapp.resx.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ResxService {

	private final ResourceConfig config;
	private final ResourceService resx;

	private final UserService serviceUser;
	private final CorpService serviceCorp;

	public Resource getResxStockholdersList(Long idxUser, Long idxCorp) {
		//
		//	todo: 권한체크, 어노테이션 or 구현
		//
		return resx.resource(serviceCorp.getResxStockholdersListPath(idxCorp));
	}
}
