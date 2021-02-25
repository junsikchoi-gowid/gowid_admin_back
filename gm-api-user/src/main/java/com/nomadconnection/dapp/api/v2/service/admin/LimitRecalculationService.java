package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.api.exception.api.NotRegisteredException;
import com.nomadconnection.dapp.api.exception.limit.LimitRecalculationException;
import com.nomadconnection.dapp.api.service.CorpService;
import com.nomadconnection.dapp.api.service.EmailService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculation;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculationHistory;
import com.nomadconnection.dapp.core.domain.limit.ReviewStatus;
import com.nomadconnection.dapp.core.domain.repository.limit.LimitRecalculationHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.limit.LimitRecalculationQueryRepository;
import com.nomadconnection.dapp.core.domain.repository.limit.LimitRecalculationRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.EmailDto;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationPageDto;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationPageDto.LimitRecalculationCondition;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationRequestDto;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationResponseDto;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.security.CustomUser;
import com.nomadconnection.dapp.core.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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

	private final LimitRecalculationHistoryRepository limitRecalculationHistoryRepository;

	private final LimitRecalculationQueryRepository limitRecalculationQueryRepository;

	private final UserService userService;

	private final CorpService corpService;

	private final EmailService emailService;

	@Transactional(readOnly = true)
	public Page<LimitRecalculationPageDto> findAll(LimitRecalculationCondition dto, Pageable pageable){
		List<LimitRecalculationPageDto.LimitRecalculationResult> limitRecalculations = limitRecalculationQueryRepository.findAll(dto ,pageable.getPageNumber(), 10);

		return new PageImpl(limitRecalculations, pageable, limitRecalculations.size());
	}

	@Transactional(readOnly = true)
	public LimitRecalculationResponseDto findOne(Long idxCorp) {
		Corp corp = corpService.findByCorpIdx(idxCorp);
		LimitRecalculation limitRecalculation = limitRecalculationRepository.findByCorp(corp)
			.orElseThrow(() -> new NotRegisteredException(ErrorCode.Api.NOT_FOUND, "idxCorp : " + idxCorp));

		return LimitRecalculationResponseDto.toDto(limitRecalculation, limitRecalculation.limitRecalculationHistories());
	}

	@Transactional(rollbackFor = Exception.class)
	public void requestRecalculate(CustomUser customUser, LimitRecalculationRequestDto dto) {
		User user = userService.getUser(customUser.idx());
		Corp corp = user.corp();
		List<ReviewStatus> underReview = Arrays.asList(ReviewStatus.REVIEWING, ReviewStatus.REQUESTED);

		limitRecalculationRepository.findByCorpAndReviewStatusIn(corp, underReview).ifPresent(
			limitRecalculation -> {
				throw new LimitRecalculationException(ErrorCode.Api.RECALCULATION_ALREADY_REVIEWING, limitRecalculation.corp().resCompanyNm());
			}
		);

		save(corp, dto);
//		sendToSupport(customUser, dto);
//		sendToUser(customUser, dto);
	}

	private void save(Corp corp, LimitRecalculationRequestDto dto){
		LimitRecalculation limitRecalculation = limitRecalculationRepository.findByCorp(corp).orElseGet(
			() -> LimitRecalculation.of(corp)
		);
		LimitRecalculationHistory limitRecalculationHistory = LimitRecalculationHistory.of(limitRecalculation, dto);
		limitRecalculationRepository.save(limitRecalculation);
		limitRecalculationHistoryRepository.save(limitRecalculationHistory);
	}

	private void sendToSupport(CustomUser customUser, LimitRecalculationRequestDto dto){
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

	private Map<String,Object> getSupportEmailContext(String email, LimitRecalculationRequestDto dto){
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

	private void sendToUser(CustomUser customUser, LimitRecalculationRequestDto dto){
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

	private Map<String, Object> getUserEmailContext(String userName, LimitRecalculationRequestDto dto){
		Map<String, Object> context = new HashMap<>();
		context.put("userName", userName);
		context.put("cardLimit", dto.getCardLimit());
		context.put("accountInfo", dto.getAccountInfo());
		context.put("etc", dto.getContents());
		context.put("guidance", dto.getContactType().getContact());

		return context;
	}

}
