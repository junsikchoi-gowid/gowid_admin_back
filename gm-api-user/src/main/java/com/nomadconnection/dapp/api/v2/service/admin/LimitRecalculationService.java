package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.api.exception.api.NotRegisteredException;
import com.nomadconnection.dapp.api.exception.limit.LimitAlreadyExistException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.api.service.CorpService;
import com.nomadconnection.dapp.api.service.EmailService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculation;
import com.nomadconnection.dapp.core.domain.repository.limit.LimitRecalculationQueryRepository;
import com.nomadconnection.dapp.core.domain.repository.limit.LimitRecalculationRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.EmailDto;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto.LimitRecalculationCondition;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto.LimitRecalculationDetail;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.security.CustomUser;
import com.nomadconnection.dapp.core.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nomadconnection.dapp.api.enums.EmailTemplate.LIMIT_RECALCULATION_SUPPORT;
import static com.nomadconnection.dapp.api.enums.EmailTemplate.LIMIT_RECALCULATION_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class LimitRecalculationService {

	private final LimitRecalculationRepository limitRecalculationRepository;

	private final LimitRecalculationQueryRepository limitRecalculationQueryRepository;

	private final CorpService corpService;

	private final EmailService emailService;

	@Transactional(readOnly = true)
	public Page<LimitRecalculationDto> findAll(LimitRecalculationCondition dto, Pageable pageable){
		List<LimitRecalculationDto> limitRecalculations = limitRecalculationQueryRepository.findAll(dto ,pageable.getPageNumber(), 10);

		return new PageImpl(limitRecalculations, pageable, limitRecalculations.size());
	}

	@Transactional(readOnly = true)
	public LimitRecalculationDto findByCorpAndDate(Long idxCorp, LocalDate date) {
		Corp corp = corpService.findByCorpIdx(idxCorp);
		LimitRecalculation limitRecalculation = limitRecalculationRepository.findByCorpAndDate(corp, date)
			.orElseThrow(() -> new NotRegisteredException(ErrorCode.Api.NOT_FOUND));

		return LimitRecalculationDto.from(limitRecalculation);
	}

	@Transactional(rollbackFor = Exception.class)
	public void requestRecalculate(CustomUser user, Long idxCorp, LimitRecalculationDetail dto) {
		Corp corp = corpService.findByCorpIdx(idxCorp);
		boolean exists = limitRecalculationRepository.findByCorpAndDate(corp, dto.getDate()).isPresent();
		if(exists){
			throw new LimitAlreadyExistException(ErrorCode.Api.RECALCULATION_ALREADY_EXIST);
		}

		save(corp, dto);
		sendToSupport(user, dto);
		sendToUser(user, dto);
	}

	private void save(Corp corp, LimitRecalculationDetail dto){
		LimitRecalculation limitRecalculation = LimitRecalculation.of(corp, dto);
		limitRecalculationRepository.save(limitRecalculation);
	}

	private void sendToSupport(CustomUser customUser, LimitRecalculationDetail dto){
		String[] receivers = new String[]{emailService.getSender(), emailService.getRiskTeam()};

		EmailDto sendToSupport = EmailDto.builder()
			.context(getSupportEmailContext(customUser.email(), dto))
			.sender(emailService.getSender())
			.receivers(receivers)
			.subject(LIMIT_RECALCULATION_SUPPORT.getSubject())
			.template(LIMIT_RECALCULATION_SUPPORT.getTemplate())
			.build();

		emailService.send(sendToSupport);
	}

	private Map<String,Object> getSupportEmailContext(String email, LimitRecalculationDetail dto){
		Map<String, Object> context = new HashMap<>();
		context.put("email", email);
		context.put("cardLimit", dto.getCardLimit());
		context.put("accountInfo", dto.getAccountInfo());
		context.put("etc", dto.getContents());
		context.put("guidance", dto.getContactType().getContact());
		context.put("companyName", dto.getCompanyName());
		context.put("hopeLimit", NumberUtils.addComma(dto.getHopeLimit()));

		return context;
	}

	private void sendToUser(CustomUser customUser, LimitRecalculationDetail dto){
		String email = customUser.email();

		EmailDto sendToUser = EmailDto.builder()
			.context(getUserEmailContext(customUser.getUsername(), dto))
			.sender(emailService.getSender())
			.receiver(email)
			.subject(LIMIT_RECALCULATION_USER.getSubject())
			.template(LIMIT_RECALCULATION_USER.getTemplate())
			.build();

		emailService.send(sendToUser);
	}

	private Map<String, Object> getUserEmailContext(String userName, LimitRecalculationDetail dto){
		Map<String, Object> context = new HashMap<>();
		context.put("userName", userName);
		context.put("cardLimit", dto.getCardLimit());
		context.put("accountInfo", dto.getAccountInfo());
		context.put("etc", dto.getContents());
		context.put("guidance", dto.getContactType().getContact());

		return context;
	}

}
