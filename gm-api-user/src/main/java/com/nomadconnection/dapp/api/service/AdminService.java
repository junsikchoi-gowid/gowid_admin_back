package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.AdminDto;
import com.nomadconnection.dapp.api.dto.ExternalDto;
import com.nomadconnection.dapp.core.domain.ResAccount;
import com.nomadconnection.dapp.core.domain.repository.ResAccountHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AdminService {

	private final EmailConfig config;
	private final ITemplateEngine templateEngine;

	private final JwtService jwt;
	private final JavaMailSenderImpl sender;

	private final UserService serviceUser;
	private final RiskRepository repoRisk;
	private final ResAccountHistoryRepository resAccountHistoryRepository;
	private final RiskConfigRepository repoRiskConfig;


	public ResponseEntity riskList(AdminCustomRepository.SearchRiskDto riskDto, Long idx, Pageable pageable) {

		Page<AdminCustomRepository.RiskCustomDto> resAccountPage = repoRisk.riskList(riskDto, idx, pageable);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(resAccountPage).build());
	}
}