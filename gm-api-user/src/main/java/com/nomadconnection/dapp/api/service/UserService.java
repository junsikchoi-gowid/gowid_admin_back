package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.*;
import com.nomadconnection.dapp.api.exception.*;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.api.NotRegisteredException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.api.service.expense.ExpenseService;
import com.nomadconnection.dapp.api.v2.service.scraping.FullTextService;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.common.IssuanceProgress;
import com.nomadconnection.dapp.core.domain.common.IssuanceProgressType;
import com.nomadconnection.dapp.core.domain.common.IssuanceStatusType;
import com.nomadconnection.dapp.core.domain.consent.ConsentMapping;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.corp.CorpStatus;
import com.nomadconnection.dapp.core.domain.embed.Authentication;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.StockholderFileRepository;
import com.nomadconnection.dapp.core.domain.repository.common.IssuanceProgressRepository;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentMappingRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CeoInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.ManagerRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ReceptionRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.user.AuthorityRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.res.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.user.*;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.security.CustomUser;
import com.nomadconnection.dapp.jwt.dto.TokenDto;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final EmailConfig config;
	private final JavaMailSenderImpl sender;
	private final PasswordEncoder encoder;
	private final ReceptionRepository receptionRepository;
	private final ITemplateEngine templateEngine;
	private final AuthorityRepository repoAuthority;
	private final UserRepository repo;
	private final CorpRepository repoCorp;
	private final CardIssuanceInfoRepository repoCardIssuanceInfo;
	private final ConnectedMngRepository repoConnectdMng;
	private final ConsentMappingRepository repoConsentMapping;
	private final IssuanceProgressRepository issuanceProgressRepository;
	private final RiskRepository repoRisk;
	private final RiskConfigRepository repoRiskConfig;
	private final CeoInfoRepository repoCeoInfo;
	private final ManagerRepository repoManager;
	private final StockholderFileRepository repoStockholderFile;
	private final UserRepository repoUser;

	private final JwtService jwt;
	private final FullTextService fullTextService;
	private final EmailService emailService;
	private final ExpenseService expenseService;

	public User getEnabledUserByEmailIfNotExistError(String email) {
		User user = repo.findByAuthentication_EnabledAndEmail(true, email).orElse(null);
		if (user != null) {
			return user;
		}

		// 지출관리 App 유저이면, 해당 에러코드 throw
		if (expenseService.isAppUser(email)) {
			throw new BadRequestException(ErrorCode.Api.EXPENSE_APP_USER);
		}

		throw new BadRequestException(ErrorCode.Api.NOT_FOUND, "email");
	}

	/**
	 * 사용자 엔터티 조회
	 *
	 * @param idxUser 식별자(사용자)
	 * @return 사용자 엔터티
	 */
	public User getUser(Long idxUser) {
		return repo.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().id(idxUser).build()
		);
	}

	public User findByEmail(String email){
		return repo.findByAuthentication_EnabledAndEmail(true,email).orElseThrow(
			() -> UserNotFoundException.builder()
				.email(email)
				.build()
		);
	}

	public User findByExternalId(String externalId){
		return repo.findByExternalId(externalId)
				.orElseThrow(() -> new NotRegisteredException(ErrorCode.Api.NOT_FOUND));
	}

	public boolean isPresentEmail(String email) {
		return repo.findByAuthentication_EnabledAndEmail(true, email).isPresent();
	}

	/**
	 * 사용자 정보 조회
	 *
	 * @param idxUser 식별자(사용자)
	 * @return 사용자 정보
	 */

	public UserDto getUserInfo(Long idxUser) {
		return UserDto.from(getUser(idxUser));
	}

	/**
	 * User 정보 초기화
	 *
	 * @param idxUser 식별자(사용자)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void initUserInfo(Long idxUser) {
		User user = getUser(idxUser);
        if (!ObjectUtils.isEmpty(user.corp())) {
            Long idxCorp = user.corp().idx();
            Long idxCardInfo = repoCardIssuanceInfo.findIdxByUserIdx(idxUser);
            repoCeoInfo.deleteByCardIssuanceInfoIdx(idxCardInfo);
            repoManager.deleteByCardIssuanceInfoIdx(idxCardInfo);
            repoStockholderFile.deleteByCardIssuanceInfoIdx(idxCardInfo);
            fullTextService.deleteAllShinhanFulltext(idxCorp);
            fullTextService.deleteAllLotteFulltext(idxCorp);
            repoRisk.deleteByCorpIdx(idxCorp);
            // Todo 추후 corp테이블의 idxRiskConfig 값 & AdminService 수정시 반영
            repoRiskConfig.delete(user.corp().riskConfig());
            repoRiskConfig.flush();
            repoCorp.delete(user.corp());
            repoCorp.flush();
            user.corp().riskConfig(null);
        }
        repoCardIssuanceInfo.deleteAllByUserIdx(idxUser);
        repoConnectdMng.deleteAllByUserIdx(idxUser);
        repoConsentMapping.deleteAllByUserIdx(idxUser);
        issuanceProgressRepository.deleteAllByUserIdx(idxUser);
		user.corp(null);
		user.cardCompany(null);
		user.isReset(true);
		repo.save(user);
	}

	/**
	 * 사용자 등록
	 *
	 * 브랜드 - 사용자 정보 수정
	 *
	 * @param dto 정보
	 */
	@Transactional
	public ResponseEntity<?> registerUserUpdate(UserDto.registerUserUpdate dto, Long idx) {
		// validation start
		// mail check
		if(repo.findByIdxNotAndEmailAndAuthentication_Enabled(idx, dto.getEmail(),true).isPresent()){
			throw AlreadyExistException.builder()
					.category("email")
					.resource(dto.getEmail())
					.build();
		}
		// validation end

		User user = getUser(idx);

		user.email(dto.getEmail());
		user.mdn(dto.getMdn());
		user.name(dto.getUserName());
		user.isSendSms(dto.getIsSendSms());
		user.isSendEmail(dto.getIsSendEmail());

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.data(repo.save(user))
				.build());
	}

	/**
	 * 사용자 등록
	 *
	 * 브랜드 - 사용자 비밀번호 변경
	 *
	 * @param dto 정보
	 */
	public ResponseEntity<?> registerUserPasswordUpdate(UserDto.registerUserPasswordUpdate dto, Long idx) {

		// validation 체크 필요
		User user = getUser(idx);

		user.password(encoder.encode(dto.getNewPassword()));

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.data(repo.save(user))
				.build());
	}


	/**
	 * 사용자 등록
	 *
	 * 브랜드 - 사용자 정보만 등록
	 *
	 * @param dto 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> registerBrandUser(UserDto.RegisterBrandUser dto) {

		// validation 체크

		UserDto.UserRegister userDto = new UserDto.UserRegister();
		BeanUtils.copyProperties(dto, userDto);
		userDto.setName(dto.getUserName());

		// 메일확인
		if (isPresentEmail(dto.getEmail())) {
			throw AlreadyExistException.builder()
					.category("email")
					.resource(userDto.getEmail())
					.build();
		}

		//	마스터 등록 (권한 설정 필요)
		User user = repo.save(User.builder()
				.consent(true)
				.email(userDto.getEmail())
				.password(encoder.encode(userDto.getPassword()))
				.name(userDto.getName())
				.mdn(userDto.getMdn())
				.isSendEmail(dto.getEmailReception())
				.isSendSms(dto.getSmsReception())
				.authentication(new Authentication())
				.authorities(Collections.singleton(
						repoAuthority.findByRole(Role.ROLE_MASTER).orElseThrow(
								() -> new RuntimeException("ROLE_MASTER NOT FOUND")
						)))
				.corpName(dto.getCorpName())
				.position(dto.getPosition())
				.build());
		// user info - end

		// 이용약관 매핑
		for(ConsentDto.RegDto regDto : dto.getConsents()) {
			repoConsentMapping.save(
					ConsentMapping.builder()
							.idxConsent(regDto.idxConsent)
							.idxUser(user.idx())
							.status(regDto.status)
							.build()
			);
		}

		TokenDto.TokenSet tokenSet = issueTokenSet(AccountDto.builder()
				.email(dto.getEmail())
				.password(dto.getPassword())
				.build());

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.data(tokenSet)
				.build());
	}

	/**
	 * 사용자 등록
	 *
	 * 브랜드 - 법인정보 등록
	 *
	 * @param dto 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> registerBrandCorp(Long idxUser, UserDto.RegisterBrandCorp dto) {
		//	중복체크
		if (repoCorp.findByResCompanyIdentityNo(dto.getResCompanyIdentityNo()).isPresent()) {
			if (log.isDebugEnabled()) {
				log.debug("([ registerBrandCorp ]) registerBrandCorp ALREADY EXIST, $idxUser='{}', $resCompanyIdentityNo='{}'", idxUser, dto.getResCompanyIdentityNo());
			}
			throw AlreadyExistException.builder()
					.resource(dto.getResCompanyIdentityNo())
					.build();
		}

		//	사용자 조회
		User user = getUser(idxUser);

		RiskConfig riskConfig = repoRiskConfig.save(RiskConfig.builder()
				.user(user)
				.ceoGuarantee(false)
				.cardIssuance(false)
				.ventureCertification(false)
				.vcInvestment(false)
				.depositPayment(false)
				.depositGuarantee(0F)
				.enabled(true)
				.build());

		//	법인정보 저장(상태: 대기)
		Corp corp = repoCorp.save(
				Corp.builder()
						.idx(dto.getIdx())
						.resJointRepresentativeNm(dto.getResJointRepresentativeNm())
						.resIssueOgzNm(dto.getResIssueOgzNm())
						.resCompanyNm(dto.getResCompanyNm())
						.resBusinessTypes(dto.getResBusinessTypes())
						.resBusinessItems(dto.getResBusinessItems())
						.resBusinessmanType(dto.getResBusinessmanType())
						.resCompanyIdentityNo(dto.getResCompanyIdentityNo())
						.resIssueNo(dto.getResIssueNo())
						.resJointIdentityNo(dto.getResJointIdentityNo())
						.resOpenDate(dto.getResOpenDate())
						.resOriGinalData(dto.getResOriGinalData())
						.resRegisterDate(dto.getResRegisterDate())
						.resUserAddr(dto.getResUserAddr())
						.resUserIdentiyNo(dto.getResUserIdentiyNo())
						.resUserNm(dto.getResUserNm())
						.status(CorpStatus.PENDING)
						.user(user)
						.riskConfig(riskConfig)
						.build()
		);

		//	사용자-법인 매핑
		repo.save(user.corp(corp));

		// fixme: dummy data - credit limit check
		// Long creditLimit = dto.getReqCreditLimit();

		// 법인정보 갱신(상태: 승인/거절, 법인한도)
		// corp.creditLimit(creditLimit);
		corp.status(CorpStatus.APPROVED);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(BusinessResponse.Normal.builder()
						.status(true)
						.build())
				.data(corp)
				.build());
	}


	private TokenDto.TokenSet issueTokenSet(AccountDto dto) {
		User user = findByEmail(dto.getEmail());
		if (!encoder.matches(dto.getPassword(), user.password())) {
			throw UnauthorizedException.builder()
					.account(dto.getEmail())
					.build();
		}

		boolean corpMapping = StringUtils.isEmpty(user.corp());
		boolean cardCompanyMapping = StringUtils.isEmpty(user.cardCompany());

		return jwt.issue(dto.getEmail(), user.authorities(), user.idx(), corpMapping, cardCompanyMapping);
	}


	/**
	 * 사용자 계정 찾기
	 *
	 * @param name 이름
	 * @param mdn 연락처(폰)
	 * @return 계정 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> findAccount(String name, String mdn) {
		List<String> user = repo.findByNameAndMdn(name, mdn)
				.map(User::email)
				.map(email -> email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*"))
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(BusinessResponse.builder().data(user).build());
	}

	/**
	 * 사용자 회사 설정 MAPPING
	 *
	 * @param dto 카드회사
	 * @param idxUser 사용자정보
	 * @return 계정 정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> companyCard(BrandDto.CompanyCard dto, Long idxUser) {

		User user = repo.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().id(idxUser).build()
		);

		user.cardCompany(dto.getCompanyCode());

		if (dto.getCompanyCode().equals(CardCompany.SHINHAN)) {
			MimeMessagePreparator preparator = mimeMessage -> {
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
				{
					Context context = new Context();
					{
						context.setVariable("corp", GowidUtils.getEmptyStringToString(user.corp().resCompanyNm()));
						context.setVariable("user", GowidUtils.getEmptyStringToString(user.name()));
						context.setVariable("mdn", GowidUtils.getEmptyStringToString(user.mdn()));
						context.setVariable("email", GowidUtils.getEmptyStringToString(user.email()));
					}
					helper.setFrom(config.getSender());
					helper.setTo(config.getSender());
					helper.setSubject("[Gowid] 신한 법인카드 발급 신청");
					helper.setText(templateEngine.process("issuance-gowid", context), true);
				}
			};
			sender.send(preparator);


			MimeMessagePreparator preparator2 = mimeMessage -> {
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
				{
					Context context = new Context();
					{
						context.setVariable("user", GowidUtils.getEmptyStringToString(user.name()));
						context.setVariable("email", GowidUtils.getEmptyStringToString(config.getSender()));
					}
					helper.setFrom(config.getSender());
					helper.setTo(user.email());
					helper.setSubject("[Gowid] 신한 법인카드 발급 안내");
					helper.setText(templateEngine.process("issuance-user", context), true);
				}
			};
			sender.send(preparator2);
		}

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.data(repo.save(user))
				.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> deleteUserByEmail(String email) {
		User user = findByEmail(email);

		user.authentication(Authentication.builder().enabled(false).build());
		user.enabledDate(LocalDateTime.now());

		repo.save(user);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(BusinessResponse.Normal.builder()
						.build())
				.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> passwordAuthAfter(Long idxUser, String prePassword, String afterPassword) {

		User userEmail = repo.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().id(idxUser).build()
		);

		User user = findByEmail(userEmail.email());

		if (!encoder.matches(prePassword, user.password())) {
			log.error("[passwordAuthAfter] invalid password");
			return ResponseEntity.ok().body(BusinessResponse.builder()
					.normal(BusinessResponse.Normal.builder()
							.status(false)
							.key("1")
							.value("현재 비밀번호가 맞지않음")
							.build())
					.build());
		}

		user.password(encoder.encode(afterPassword));
		User returnUser = repo.save(user);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(BusinessResponse.Normal.builder()
						.build())
				.data(returnUser)
				.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveReception(String key) {
		receptionRepository.save(
				Reception.builder()
						.receiver(key)
						.status(true)
						.build()
		);
		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity deleteReception(String key) {
		receptionRepository.deleteByReceiver(key);
		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	@Transactional(rollbackFor = Exception.class)
	public CorpDto getBrandCorp(Long idx) {
 
		Corp corp = getUser(idx).corp();

		if(corp == null){
			throw CorpNotRegisteredException.builder().build();
		}

		Long idxCorp = getUser(idx).corp().idx();

		Optional<CorpDto> corpDto = repoCorp.findById(idxCorp).map(CorpDto::from);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.data(corpDto.get())
				.build()); 
	}

	public ResponseEntity registerUserConsent(UserDto.RegisterUserConsent dto, Long idxUser) {

		User user = repo.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().id(idxUser).build()
		);

		// 이용약관 매핑
		for(ConsentDto.RegDto regDto : dto.getConsents()) {
			repoConsentMapping.save(
					ConsentMapping.builder()
							.idxConsent(regDto.idxConsent)
							.idxUser(user.idx())
							.status(regDto.status)
							.build()
			);
		}

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.data(user)
				.build());
	}

	@Transactional
	public ResponseEntity<UserDto.IssuanceProgressRes> issuanceProgress(Long userIdx) {
		User user = repo.findById(userIdx).orElseThrow(
				() -> new BadRequestException(ErrorCode.Api.NOT_FOUND, "userIdx=" + userIdx)
		);

		IssuanceProgress issuanceProgress = issuanceProgressRepository.findById(userIdx).orElse(
				IssuanceProgress.builder()
						.userIdx(userIdx)
						.corpIdx(!ObjectUtils.isEmpty(user.corp()) ? user.corp().idx() : null)
						.progress(IssuanceProgressType.NOT_SIGNED)
						.status(IssuanceStatusType.SUCCESS)
						.build()
		);

		return ResponseEntity.ok().body(UserDto.IssuanceProgressRes.builder()
				.progress(issuanceProgress.getProgress())
				.status(issuanceProgress.getStatus())
				.cardCompany(!ObjectUtils.isEmpty(issuanceProgress.getCardCompany()) ? issuanceProgress.getCardCompany().name() : CardCompany.SHINHAN.name())
				.build());
	}

	public void saveUser(User user) {
		repo.save(user);
	}

	public void saveIssuanceProgress(Long userIdx, IssuanceProgressType progressType, IssuanceStatusType statusType) {
		try {
			issuanceProgressRepository.save(IssuanceProgress.builder()
					.userIdx(userIdx)
					.corpIdx(getCorpIdx(userIdx))
					.progress(progressType)
					.status(statusType).build());
		} catch (Exception e) {
			log.warn("[saveIssuanceProgress] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage());
		}
	}

	public void saveIssuanceProgress(Long userIdx, IssuanceProgressType progressType, IssuanceStatusType statusType, CardCompany cardCompany) {
		try {
			issuanceProgressRepository.save(IssuanceProgress.builder()
					.userIdx(userIdx)
					.corpIdx(getCorpIdx(userIdx))
					.progress(progressType)
					.cardCompany(cardCompany)
					.status(statusType).build());
		} catch (Exception e) {
			log.warn("[saveIssuanceProgress] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage());
		}
	}

	@Transactional(noRollbackFor = Exception.class)
	public void saveIssuanceProgFailed(Long userIdx, IssuanceProgressType progressType) {
		saveIssuanceProgress(userIdx, progressType, IssuanceStatusType.FAILED);
	}

	@Transactional(noRollbackFor = Exception.class)
	public void saveIssuanceProgSuccess(Long userIdx, IssuanceProgressType progressType) {
		saveIssuanceProgress(userIdx, progressType, IssuanceStatusType.SUCCESS);
	}

	@Transactional(noRollbackFor = Exception.class)
	public void saveIssuanceProgFailed(Long userIdx, IssuanceProgressType progressType, CardCompany cardCompany) {
		saveIssuanceProgress(userIdx, progressType, IssuanceStatusType.FAILED, cardCompany);
	}

	@Transactional(noRollbackFor = Exception.class)
	public void saveIssuanceProgSuccess(Long userIdx, IssuanceProgressType progressType, CardCompany cardCompany) {
		saveIssuanceProgress(userIdx, progressType, IssuanceStatusType.SUCCESS, cardCompany);
	}

	private Long getCorpIdx(Long userIdx) {
		if (ObjectUtils.isEmpty(userIdx)) {
			return null;
		}
		User user = repo.findById(userIdx).orElse(null);
		if (user == null) {
			return null;
		}
		return !ObjectUtils.isEmpty(user.corp()) ? user.corp().idx() : null;
	}

	/**
	 * 사용자 정보 조회
	 *
	 * @param dto
	 */

	public ResponseEntity limitReview(Long idxUser, UserDto.LimitReview dto) {
		String guidance = "";
		if (dto.getEnablePhone() && dto.getEnableEmail()) {
			guidance = "휴대폰 / 이메일";
		} else if (dto.getEnableEmail()) {
			guidance = "이메일";
		} else if (dto.getEnablePhone()) {
			guidance = "휴대폰";
		}

		String finalGuidance = guidance;
		User user = repo.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().build()
		);

		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			{
				Context context = new Context();
				{
					context.setVariable("userName", GowidUtils.getEmptyStringToString(user.name()));
					context.setVariable("cardLimit", GowidUtils.getEmptyStringToString(dto.getCardLimit()));
					context.setVariable("accountInfo", GowidUtils.getEmptyStringToString(dto.getAccountInfo()));
					context.setVariable("etc", GowidUtils.getEmptyStringToString(dto.getEtc()));
					context.setVariable("guidance", GowidUtils.getEmptyStringToString(finalGuidance));
				}
				helper.setFrom(config.getSender());
				helper.setTo(user.email());
				helper.setSubject("[Gowid] 한도 재심사 요청 안내");
				helper.setText(templateEngine.process("limit-review", context), true);
			}
		};
		sender.send(preparator);

		MimeMessagePreparator preparatorSupport = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			{
				Context context = new Context();
				{
					context.setVariable("email", GowidUtils.getEmptyStringToString(user.email()));
					context.setVariable("cardLimit", GowidUtils.getEmptyStringToString(dto.getCardLimit()));
					context.setVariable("accountInfo", GowidUtils.getEmptyStringToString(dto.getAccountInfo()));
					context.setVariable("etc", GowidUtils.getEmptyStringToString(dto.getEtc()));
					context.setVariable("guidance", GowidUtils.getEmptyStringToString(finalGuidance));
					context.setVariable("companyName", GowidUtils.getEmptyStringToString(dto.getCompanyName()));
					context.setVariable("hopeLimit", GowidUtils.getEmptyStringToString(dto.getHopeLimit()));
				}
				helper.setFrom(config.getSender());
				String[] receivers = {config.getSender(), config.getRiskteam()};
				helper.setTo(receivers);
				helper.setSubject("[Gowid] 한도 재심사 요청 안내");
				helper.setText(templateEngine.process("limit-review-support", context), true);
			}
		};
		sender.send(preparatorSupport);

		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	public UserDto.ExternalIdRes getUserExternalId(CustomUser customUser) {
		User user = repo.findById(customUser.idx()).orElseThrow(() -> new BadRequestException(ErrorCode.Api.NOT_FOUND, "userId"));
		if (StringUtils.isEmpty(user.externalId())) {
			user.externalId(UUID.randomUUID().toString());
			repo.save(user);
		}
		return UserDto.ExternalIdRes.builder().externalId(user.externalId()).build();
	}

    @Transactional(readOnly = true)
	public void sendEmailDeleteAccount(String email, String password, String reason) {
		User user = findByEmail(email);
		if (!encoder.matches(password, user.password())) {
			log.error("[sendEmailDeleteAccount] invalid password");
			throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED);
		}
		emailService.sendDeleteAccountEmailtoUser(user);
		emailService.sendDeleteAccountEmailtoSupport(user, reason);
    }

	public void enableAccount(Long idxUser) {
		User user = repo.findById(idxUser).orElse(null);
		user.authentication().setEnabled(true);
		user.enabledDate(null);
		repo.save(user);
	}
}
