package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.*;
import com.nomadconnection.dapp.api.exception.AlreadyExistException;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.UnauthorizedException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.api.NotRegisteredException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.api.service.expense.ExpenseService;
import com.nomadconnection.dapp.api.v2.service.scraping.FullTextService;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.consent.ConsentMapping;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.corp.CorpStatus;
import com.nomadconnection.dapp.core.domain.embed.Authentication;
import com.nomadconnection.dapp.core.domain.embed.UserReception;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.StockholderFileRepository;
import com.nomadconnection.dapp.core.domain.repository.connect.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentMappingRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CeoInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpBranchRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.ManagerRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ReceptionRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.user.AuthorityRepository;
import com.nomadconnection.dapp.core.domain.repository.user.EventsRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.risk.Risk;
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
	private final UserRepository repoUser;
	private final CorpRepository repoCorp;
	private final EventsRepository repoEvents;
	private final CardIssuanceInfoRepository repoCardIssuanceInfo;
	private final ConnectedMngRepository repoConnectdMng;
	private final ConsentMappingRepository repoConsentMapping;
	private final RiskRepository repoRisk;
	private final RiskConfigRepository repoRiskConfig;
	private final CeoInfoRepository repoCeoInfo;
	private final ManagerRepository repoManager;
	private final StockholderFileRepository repoStockholderFile;
	private final CorpBranchRepository repoCorpBranch;

	private final JwtService jwt;
	private final FullTextService fullTextService;
	private final EmailService emailService;
	private final ExpenseService expenseService;
	private final SurveyService surveyService;


	public User getEnabledUserByEmailIfNotExistError(String email) {
		return repoUser.findByAuthentication_EnabledAndEmail(true, email).orElse(null);
	}

	/**
	 * 사용자 엔터티 조회
	 *
	 * @param idxUser 식별자(사용자)
	 * @return 사용자 엔터티
	 */
	public User getUser(Long idxUser) {
		return repoUser.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().id(idxUser).build()
		);
	}

	public User findByEmail(String email){
		return repoUser.findByAuthentication_EnabledAndEmail(true,email).orElseThrow(
			() -> UserNotFoundException.builder()
				.email(email)
				.build()
		);
	}

	public User findByExternalId(String externalId){
		return repoUser.findByExternalId(externalId)
				.orElseThrow(() -> new NotRegisteredException(ErrorCode.Api.NOT_FOUND));
	}

	public boolean isPresentEmail(String email) {
		return repoUser.findByAuthentication_EnabledAndEmail(true, email).isPresent();
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

	@Transactional(rollbackFor = Exception.class)
	public UserDto addMember(Long idxAdminUser, UserDto.MemberRegister member) {
		Long idxCorp = getCorpIdx(idxAdminUser);

		if(idxCorp == null) {
			throw new BadRequestException(ErrorCode.Api.CORP_IS_NOT_REGISTERED);
		}
		User adminUser = getUser(idxAdminUser);

		try {
			findByEmail(member.getEmail());
			throw AlreadyExistException.builder()
					.category("email")
					.resource(member.getEmail())
					.build();
		} catch (UserNotFoundException e){
			Corp corp = repoCorp.findById(idxCorp)
					.orElseThrow(() -> new BadRequestException(ErrorCode.Api.CORP_NOT_BUSINESS));
			String plainPassword = member.getPassword();

			User user = User.builder()
					.consent(false)
					.email(member.getEmail())
					.password(encoder.encode(plainPassword))
					.hasTmpPassword(member.isHasTmpPassword())
					.name(member.getName())
					.mdn(null)
					.reception(new UserReception(false, false))
					.authentication(new Authentication())
					.authorities(Collections.singleton(
							repoAuthority.findByRole(member.getRole()).orElseThrow(
									() -> new RuntimeException(member.getRole() + " NOT FOUND")
							)))
					.corpName(corp.resCompanyNm())
					.cardCompany(adminUser.cardCompany())
					.position(null)
					.build();

			user.corp(corp);
			repoUser.save(user);

			return UserDto.from(user);
		} catch(Exception e) {
			throw e;
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public User changeMemberInfo(Long idxAdminUser, String email, UserDto.MemberRegister memberInfo) {
		User adminUser = repoUser.findById(idxAdminUser).orElseThrow(() -> UserNotFoundException.builder().build());
		User targetUser = findByEmail(email);
		boolean isSameCorp = adminUser.corp().idx() == targetUser.corp().idx();

		if(!isSameCorp) {
			throw UserNotFoundException.builder().email(email).build();
		}

		if(memberInfo.getEmail() != null) {
			targetUser.email(memberInfo.getEmail());
		}

		if(memberInfo.getName() != null) {
			targetUser.name(memberInfo.getName());
		}

		if(memberInfo.getPassword() != null) {
			targetUser.password(encoder.encode(memberInfo.getPassword()));
		}

		if(memberInfo.getRole() != null) {
			Set<Authority> list = targetUser.authorities();

			//TODO hyuntak to refactor this
			Authority a = new Authority(new Long(Role.ROLE_MASTER.ordinal()+1), Role.ROLE_MASTER);
			Authority b = new Authority(new Long(Role.ROLE_VIEWER.ordinal()+1), Role.ROLE_VIEWER);
			Authority c = new Authority(new Long(Role.ROLE_EXPENSE_MANAGER.ordinal()+1), Role.ROLE_EXPENSE_MANAGER);
			Authority d = new Authority(new Long(Role.ROLE_MEMBER.ordinal()+1), Role.ROLE_MEMBER);
			list.remove(a);
			list.remove(b);
			list.remove(c);
			list.remove(d);

			list.add(
					repoAuthority.findByRole(memberInfo.getRole()).orElseThrow(
							() -> new RuntimeException(memberInfo.getRole() + " NOT FOUND")
					));
			targetUser.authorities(list);
		}
		return repoUser.save(targetUser);
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean removeMember(Long idxAdminUser, String email) {
		log.info("[removeMember] to be deleted ({})", email);

		try {
			User adminUser = repoUser.findById(idxAdminUser).orElse(null);
			User targetUser = findByEmail(email);
			log.debug("admin {} targetUer {} {} ", adminUser.corp().idx(), targetUser.corp().idx(), targetUser.idx());
			if(targetUser == null || !adminUser.corp().idx().equals(targetUser.corp().idx())) {
				throw UserNotFoundException.builder().build();
			}

			// TODO hyuntak 사용자 관련 링크 제거. 향후 리스크 생성을 사용자가 아닌 법인 별로 해야 할 듯
			List<Risk> risks = repoRisk.findByUser(targetUser);

			for(Risk risk: risks) {
				risk.user(null);
			}

			Optional<RiskConfig> riskConfig = repoRiskConfig.findByUser(targetUser);
			if(riskConfig.isPresent()) {
				riskConfig.get().user(null);
			}

//			// TODO hyuntak survey answer 삭제. 향후 서베이 정책이 반영되면 삭제되어야 할 코드
			try {
				SurveyDto surveyDto = surveyService.findAnswerByUser(targetUser);
				surveyService.deleteAnswer(targetUser, surveyDto);
			} catch(Exception e) {
			} finally {
				targetUser.authorities().clear();
				repoUser.delete(targetUser);
				return true;
			}
		} catch (Exception e) {
			log.error("[removeMember] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage());
			return false;
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void initializePassword(String email) {
		User member = findByEmail(email);
		if(member == null) {
			throw UserNotFoundException.builder().email(email).build();
		}
		String garbagePassword = encoder.encode(UUID.randomUUID().toString().substring(0,8));
		member.password(garbagePassword);  //to set random
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
			List<Long> cardIssuanceInfoIdxs = repoCardIssuanceInfo.findAllIdxByUserIdx(user.idx());
			for (Long idxCardInfo : cardIssuanceInfoIdxs) {
				repoCeoInfo.deleteByCardIssuanceInfoIdx(idxCardInfo);
				repoManager.deleteByCardIssuanceInfoIdx(idxCardInfo);
				repoStockholderFile.deleteByCardIssuanceInfoIdx(idxCardInfo);
			}
			fullTextService.deleteAllShinhanFulltext(idxCorp);
			fullTextService.deleteAllLotteFulltext(idxCorp);
			repoRisk.deleteByCorpIdx(idxCorp);
			// Todo 추후 corp테이블의 idxRiskConfig 값 & AdminService 수정시 반영
			if (!ObjectUtils.isEmpty(user.corp().riskConfig())) {
				user.corp().riskConfig().user(null);
				user.corp().riskConfig().corp(null);
				user.corp().riskConfig(null);
				repoRiskConfig.deleteByCorpIdx(idxCorp);
			}
			user.corp().user(null);
			for (CardIssuanceInfo cardIssuanceInfo : user.corp().cardIssuanceInfo()) {
				cardIssuanceInfo.corp(null);
			}
			repoConnectdMng.deleteAllByUserIdx(idxUser);

			user.corp(null);
			repoCorp.deleteCorpByIdx(idxCorp);
		}
		repoCardIssuanceInfo.deleteAllByUserIdx(idxUser);
		repoConsentMapping.deleteAllByUserIdx(idxUser);
		user.cardCompany(null);
		user.isReset(true);
		repoUser.save(user);
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
		if(repoUser.findByIdxNotAndEmailAndAuthentication_Enabled(idx, dto.getEmail(),true).isPresent()){
			throw AlreadyExistException.builder()
					.category("email")
					.resource(dto.getEmail())
					.build();
		}
		// validation end

		User user = getUser(idx);
		String orgEmail = user.email();

		if(StringUtils.hasText(dto.getEmail())) { user.email(dto.getEmail()); }
		if(StringUtils.hasText(dto.getMdn())) { user.mdn(dto.getMdn()); }
		if(StringUtils.hasText(dto.getUserName())) { user.name(dto.getUserName()); }
		if(dto.getIsSendEmail() != null) {user.reception().setIsSendEmail(dto.getIsSendEmail()); }
		if(dto.getIsSendSms() != null) { user.reception().setIsSendSms(dto.getIsSendSms()); }

		try {
			expenseService.updateExpenseUserProfile(orgEmail, dto.getUserName(), dto.getMdn(), dto.getEmail());
		} catch (Exception e) {
			log.error(e.getMessage()); // just skip to throw exception
		}

		return ResponseEntity.ok().body(BusinessResponse.builder().build());
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
		User user = repoUser.save(User.builder()
				.consent(true)
				.email(userDto.getEmail())
				.password(encoder.encode(userDto.getPassword()))
				.name(userDto.getName())
				.mdn(userDto.getMdn())
				.reception(new UserReception().builder()
					.isSendEmail(dto.getEmailReception())
					.isSendSms(dto.getSmsReception())
					.build())
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
			ConsentMapping consentMapping = repoConsentMapping.findByIdxUserAndIdxConsent(user.idx(), regDto.idxConsent);
			if (consentMapping == null) {
				repoConsentMapping.save(
					ConsentMapping.builder()
						.idxConsent(regDto.idxConsent)
						.idxUser(user.idx())
						.status(regDto.status)
						.build()
				);
			} else {
				repoConsentMapping.save(
					consentMapping.status(regDto.status)
				);
			}
		}

		TokenDto.TokenSet tokenSet = issueTokenSet(AccountDto.builder()
				.email(dto.getEmail())
				.password(dto.getPassword())
				.build());

		// 이벤트 정보
		if(!StringUtils.isEmpty(dto.getEventName())){
			repoEvents.save(Events.builder()
					.idxUser(user.idx())
					.createUser(user.idx())
					.eventName(dto.getEventName())
					.build());
		}

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
		repoUser.save(user.corp(corp));

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

	@Transactional(readOnly = true)
	public TokenDto.TokenSet issueTokenSet(AccountDto dto) {
		User user = findByEmail(dto.getEmail());
		if (!encoder.matches(dto.getPassword(), user.password())) {
			throw UnauthorizedException.builder()
					.account(dto.getEmail())
					.build();
		}
		//	사용자 조회
		String role = Role.ROLE_MASTER.name();

		boolean corpMapping = StringUtils.isEmpty(user.corp());
		boolean cardCompanyMapping = StringUtils.isEmpty(user.cardCompany());

		Set<Authority> authrities = user.authorities();

		if(!ObjectUtils.isEmpty(user.corp())){
			if(!ObjectUtils.isEmpty(user.corp().authorities())) authrities.addAll(user.corp().authorities());
		}

		return jwt.issue(dto.getEmail(), authrities, user.idx(), corpMapping, cardCompanyMapping, user.hasTmpPassword(), role);
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> deleteUserByEmail(String email) {
		User user = findByEmail(email);

		user.authentication(Authentication.builder().enabled(false).build());
		user.enabledDate(LocalDateTime.now());

		repoUser.save(user);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(BusinessResponse.Normal.builder()
						.build())
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
	public ResponseEntity getBrandCorp(Long idx) {
 
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

	@Transactional(rollbackFor = Exception.class)
	public CorpDto.CorpInfoDto getBrandCorpBranch(Long idxUser) {

		User user = repoUser.findById(idxUser).orElseThrow(() -> UserNotFoundException.builder().build());
		if( user.corp() == null ){
			throw CorpNotRegisteredException.builder().build();
		}

		CorpDto corpDto = Optional.ofNullable(user.corp()).map(CorpDto::from).orElseThrow(
			() -> CorpNotRegisteredException.builder().build()
		);

		List<CorpDto.CorpBranchDto> corpBranch = repoCorpBranch.findByCorp(user.corp()).stream().map(CorpDto.CorpBranchDto::from).collect(Collectors.toList());

		return CorpDto.CorpInfoDto.builder()
			.corpBranchDtos(corpBranch)
			.corpDto(corpDto)
			.build();
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity registerUserConsent(UserDto.RegisterUserConsent dto, Long idxUser) {
		CardType cardType = dto.getCardType();
		User user = repoUser.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().id(idxUser).build()
		);


		// 이용약관 매핑
		for(ConsentDto.RegDto regDto : dto.getConsents()) {
			ConsentMapping consentMapping = repoConsentMapping.findByIdxUserAndIdxConsent(user.idx(), regDto.idxConsent);
			if (consentMapping == null) {
				repoConsentMapping.save(
					ConsentMapping.builder()
						.idxConsent(regDto.idxConsent)
						.idxUser(idxUser)
						.status(regDto.status)
						.cardType(cardType)
						.build()
				);
			} else {
				repoConsentMapping.save(
					consentMapping.status(regDto.status)
				);
			}
		}

		if (CardType.KISED.equals(cardType)) {
			user.cardCompany(CardCompany.SHINHAN);

			CardIssuanceInfo cardIssuanceInfo = repoCardIssuanceInfo.findByUserAndCardType(user, CardType.KISED).orElseGet(
				() -> CardIssuanceInfo.builder()
					.corp(user.corp())
					.user(user)
					.cardType(CardType.KISED)
					.cardCompany(CardCompany.SHINHAN)
					.issuanceStatus(IssuanceStatus.INPROGRESS)
					.build()
			);
			repoCardIssuanceInfo.save(cardIssuanceInfo);
		}

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.data(user)
				.build());
	}

	public void saveUser(User user) {
		repoUser.save(user);
	}

	private Long getCorpIdx(Long userIdx) {
		if (ObjectUtils.isEmpty(userIdx)) {
			return null;
		}
		User user = repoUser.findById(userIdx).orElse(null);
		if (user == null) {
			return null;
		}
		return !ObjectUtils.isEmpty(user.corp()) ? user.corp().idx() : null;
	}

	private Corp getCorBpyRegistrationNumber(String registrationNumber) {
		return repoCorp.findByResCompanyIdentityNo(registrationNumber).orElse(null);
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
		User user = repoUser.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().build()
		);

		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			{
				Context context = new Context();
				{
					context.setVariable("companyName", GowidUtils.getEmptyStringToString(dto.getCompanyName()));
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
		User user = repoUser.findById(customUser.idx()).orElseThrow(() -> new BadRequestException(ErrorCode.Api.NOT_FOUND, "userId"));
		if (StringUtils.isEmpty(user.externalId())) {
			user.externalId(UUID.randomUUID().toString());
			repoUser.save(user);
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

	@Transactional
	public void enableAccount(Long idxUser) {
		User user = repoUser.findById(idxUser).orElse(null);
		user.authentication().setEnabled(true);
		user.enabledDate(null);
		repoUser.save(user);
	}

	@Transactional(readOnly = true)
	public UserDto.EventsInfo getEvents(CustomUser user, String eventName) {

		return repoEvents.findTopByEventNameAndIdxUser(eventName, user.idx()).map(UserDto.EventsInfo::from).orElse(null);
	}
}
