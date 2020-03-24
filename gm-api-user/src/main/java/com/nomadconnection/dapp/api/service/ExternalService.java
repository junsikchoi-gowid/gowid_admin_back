package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.ExternalDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.core.domain.ResAccount;
import com.nomadconnection.dapp.core.domain.ResAccountHistory;
import com.nomadconnection.dapp.core.domain.Risk;
import com.nomadconnection.dapp.core.domain.RiskConfig;
import com.nomadconnection.dapp.core.domain.repository.ResAccountHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.RiskRepository;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.thymeleaf.ITemplateEngine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

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