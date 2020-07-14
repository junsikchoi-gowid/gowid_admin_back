package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.ExternalDto;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ExternalService {

	private final EmailConfig config;
	private final ITemplateEngine templateEngine;

	private final JwtService jwt;
	private final JavaMailSenderImpl sender;

	private final UserService serviceUser;
	private final ResAccountRepository repoResAccount;
	private final ResAccountHistoryRepository resAccountHistoryRepository;
	private final RiskConfigRepository repoRiskConfig;


	public Page getData(Pageable page, ExternalDto externalDto) {

		Page<ResAccount> resAccountPage = repoResAccount.findExternalAccount(page, (long) 5);

		List<ResAccount> rs = resAccountPage.getContent();

		return resAccountPage;
	}
}