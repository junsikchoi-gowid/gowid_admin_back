package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.*;
import com.nomadconnection.dapp.api.exception.*;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.consent.ConsentMapping;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.corp.CorpStatus;
import com.nomadconnection.dapp.core.domain.corp.Dept;
import com.nomadconnection.dapp.core.domain.embed.Authentication;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentMappingRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.DeptRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ReceptionRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.IssuanceProgressRepository;
import com.nomadconnection.dapp.core.domain.repository.user.AlarmRepository;
import com.nomadconnection.dapp.core.domain.repository.user.AuthorityRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.repository.user.VerificationCodeRepository;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.shinhan.IssuanceProgress;
import com.nomadconnection.dapp.core.domain.shinhan.IssuanceProgressType;
import com.nomadconnection.dapp.core.domain.shinhan.IssuanceStatusType;
import com.nomadconnection.dapp.core.domain.user.*;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
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
	private final UserRepository repo;
	private final RiskConfigRepository repoRiskConfig;
	private final ConsentMappingRepository repoConsentMapping;
	private final ReceptionRepository receptionRepository;
	private final CorpRepository repoCorp;
	private final DeptRepository repoDept;
	private final AuthorityRepository repoAuthority;
	private final VerificationCodeRepository repoVerificationCode;
	private final AlarmRepository repoAlarm;
	private final ITemplateEngine templateEngine;
	private final IssuanceProgressRepository issuanceProgressRepository;

	private final JwtService jwt;

	/**
	 * 사용자 엔터티 조회
	 *
	 * @param idxUser 식별자(사용자)
	 * @return 사용자 엔터티
	 */
	User getUser(Long idxUser) {
		return repo.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().id(idxUser).build()
		);
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
	 * 사용자 등록
	 *
	 * - 인증코드가 설정되어 있는 경우, 초대된 멤버의 가입으로 처리
	 *
	 * @param dto 등록정보
	 */
	@Transactional(rollbackFor = Exception.class)
	public void registerUser(UserDto.UserRegister dto) {
		//	이메일 중복 체크


		if (repo.findByAuthentication_EnabledAndEmail(true,dto.getEmail()).isPresent()) {
			throw AlreadyExistException.builder()
					.category("email")
					.resource(dto.getEmail())
					.build();
		}
		if (dto.getVerificationCode() == null) {
			//
			//	마스터 등록
			//
			repo.save(User.builder()
					.consent(dto.isConsent())
					.email(dto.getEmail())
					.password(encoder.encode(dto.getPassword()))
					.name(dto.getName())
					.mdn(dto.getMdn())
					.authentication(new Authentication())
					.authorities(Collections.singleton(
							repoAuthority.findByRole(Role.ROLE_MASTER).orElseThrow(
									() -> new RuntimeException("ROLE_MASTER NOT FOUND")
							)))
					.build());
		} else {
			//
			//	어드민, 멤버 등록
			//
			repoVerificationCode.findByVerificationKeyAndCode(dto.getEmail(), dto.getVerificationCode()).orElseThrow(
					() -> VerificationCodeMismatchedException.builder()
							.code(dto.getVerificationCode())
							.build()
			);
			User user = repo.findByAuthentication_EnabledAndEmail(true,dto.getEmail()).orElseThrow(
					() -> UserNotFoundException.builder()
							.email(dto.getEmail())
							.build()
			);
			user.password(encoder.encode(dto.getPassword()));
			user.name(dto.getName());
			user.mdn(dto.getMdn());
			user.authentication().setEnabled(true);
		}
	}

	/**
	 * 사용자(카드멤버) 초대
	 *
	 * - 사용자(어드민: 마스터에 의해 초대) 등록
	 * - 사용자(멤버: 마스터에 의해 초대) 등록
	 *
	 * @param idxUser 식별자(사용자)
	 * @param dto 멤버 정보 - 이름, 이메일, 권한(관리자/일반사용자), 부서(식별자), 월한도
	 */
	@Transactional(rollbackFor = Exception.class)
	public void inviteMember(Long idxUser, UserDto.MemberRegister dto) {
		//	사용자 조회
		User user = getUser(idxUser);
		//	부서 없음
		Dept dept = repoDept.findById(dto.getIdxDept()).orElseThrow(
				() -> DeptNotFoundException.builder()
						.dept(dto.getIdxDept())
						.build()
		);
		//	법인 미등록 상태
		if (user.corp() == null) {
			throw CorpNotRegisteredException.builder()
					.account(user.email())
					.build();
		}
		//	마스터 권한으로 초대
		if (MemberAuthority.MASTER.equals(dto.getAuthority())) {
			throw NotAllowedMemberAuthorityException.builder()
					.account(dto.getEmail())
					.authority(dto.getAuthority())
					.build();
		}
		//	권한
		Authority authority = repoAuthority.findByRole(MemberAuthority.ADMIN.equals(dto.getAuthority()) ? Role.ROLE_ADMIN : Role.ROLE_MEMBER).orElseThrow(
				() -> new RuntimeException("ROLE_ADMIN or ROLE_MEMBER NOT FOUND")
		);
		//	멤버 초대
		repo.save(User.builder()
				.email(dto.getEmail())
				.name(dto.getName())
				.dept(dept)
				.corp(user.corp())
				.authentication(new Authentication().setEnabled(false))
				.authorities(Collections.singleton(authority))
				.creditLimit(dto.getCreditLimit())
				.build());
		//	인증코드 등록
		String code = String.format("%04d", new Random().nextInt(10000));
		repoVerificationCode.save(VerificationCode.builder()
				.verificationKey(dto.getEmail())
				.code(code)
				.build());
		//	메일 발송
		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			helper.setFrom(config.getSender());
			helper.setTo(dto.getEmail());
			helper.setSubject("[MyCard] 멤버초대");
			helper.setText("Email: " + dto.getEmail() + ", VerificationCode: " + code, false);
		};
		sender.send(preparator);
	}

	/**
	 * 부서정보 설정
	 *
	 * @param idxUser 식별자(사용자)
	 * @param idxMember 식별자(사용자:변경대상)
	 * @param idxDept 식별자(부서)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateDept(Long idxUser, Long idxMember, Long idxDept) {
		User user = getUser(idxUser);
		{
			//
			//	todo: check user authorities
			//
		}
		User member = getUser(idxMember);
		Dept dept = repoDept.findById(idxDept).orElseThrow(
				() -> DeptNotFoundException.builder().dept(idxDept).build()
		);
		user.dept(dept);
	}

	/**
	 * 부서정보 제거
	 *
	 * @param idxUser 식별자(사용자)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void removeDept(Long idxUser, Long idxMember) {
		User user = getUser(idxUser);
		{
			//
			//	todo: check user authorities
			//
		}
		User member = getUser(idxMember);
		member.dept(null);
	}






	/**
	 * 이용내역 리스트 분기
	 */
	private List<Long> getConsentIdxList(List<ConsentDto.RegDto> consents) {
		Long[] l = new Long[consents.size()];
		int index = 0 ;
		for(ConsentDto.RegDto r : consents){
			l[index] = r.idxConsent;
			index++;
		}

		List<Long> returnList = new ArrayList<>();
		Collections.addAll(returnList,l);
		return returnList;
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
		if (repo.findByAuthentication_EnabledAndEmail(true,userDto.getEmail()).isPresent()) {
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
		User user = repo.findByAuthentication_EnabledAndEmail(true,dto.getEmail()).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(dto.getEmail())
						.build()
		);
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
					helper.setSubject("신한 법인카드 발급 신청");
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
					helper.setSubject("Gowid 신한 법인카드 발급 안내");
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
	public ResponseEntity<?> deleteEmail(String email) {
		User user = repo.findByAuthentication_EnabledAndEmail(true,email).orElseThrow(() -> new RuntimeException("UserNotFound"));

		user.authentication(Authentication.builder().enabled(false).build());
		user.enabledDate(LocalDateTime.now());

		repo.save(user);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(BusinessResponse.Normal.builder()
						.build())
				.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> passwordAuthPre(String email, String value, String password) {

		if(repoVerificationCode.findByVerificationKeyAndCode(email, value).isPresent()){
			User user = repo.findByAuthentication_EnabledAndEmail(true,email).orElseThrow(
					() -> UserNotFoundException.builder()
							.email(email)
							.build()
			);

			repoVerificationCode.deleteById(email);

			log.debug("pass $pass='{}'" , encoder.encode(password));
			user.password(encoder.encode(password));
			repo.save(user);
		}else{

			return ResponseEntity.ok().body(BusinessResponse.builder()
					.normal(BusinessResponse.Normal.builder()
							.status(false).value("비밀번호 or Email 이 맞지않음").build())
					.build());
		}

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.normal(BusinessResponse.Normal.builder().build())
				.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> passwordAuthAfter(Long idxUser, String prePassword, String afterPassword) {

		User userEmail = repo.findById(idxUser).orElseThrow(
				() -> UserNotFoundException.builder().id(idxUser).build()
		);

		User user = repo.findByAuthentication_EnabledAndEmail(true, userEmail.email()).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(userEmail.email())
						.build()
		);

		if (!encoder.matches(prePassword, user.password())) {
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
	public ResponseEntity getBrandCorp(Long idx) {

		Long idxCorp = getUser(idx).corp().idx();
		Optional<CorpDto> corp = repoCorp.findById(idxCorp).map(CorpDto::from);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.data(corp.get())
				.build());
	}

	public ResponseEntity saveAlarm(BrandDto.Alarm dto) {

		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			{
				Context context = new Context();
				{
					context.setVariable("contents1", dto.getCorpName());
					context.setVariable("contents2", dto.getName());
					context.setVariable("contents3", dto.getEmail());
					context.setVariable("contents4", dto.isVentureCertification());
					context.setVariable("contents5", dto.isVcInvestment());
				}

				helper.setFrom(config.getSender());
				helper.setTo(config.getSender());
				helper.setSubject("[Gowid 알림] ");
				helper.setText(templateEngine.process("mail-template-alarm", context), true);
			}
		};
		sender.send(preparator);

		return ResponseEntity.ok().body(BusinessResponse.builder()
				.data(repoAlarm.save(Alarm.builder()
						.corpName(dto.getCorpName())
						.email(dto.getEmail())
						.name(dto.getName())
						.vcInvestment(dto.isVcInvestment())
						.ventureCertification(dto.isVentureCertification())
						.build()))
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

	public ResponseEntity<UserDto.IssuanceProgressRes> issuanceProgress(Long userIdx) {
		repo.findById(userIdx).orElseThrow(
				() -> new BadRequestException(ErrorCode.Api.NOT_FOUND, "userIdx=" + userIdx)
		);

		IssuanceProgress issuanceProgress = issuanceProgressRepository.findById(userIdx).orElse(
				IssuanceProgress.builder()
						.userIdx(userIdx)
						.progress(IssuanceProgressType.NOT_SIGNED)
						.status(IssuanceStatusType.SUCCESS)
						.build()
		);

		return ResponseEntity.ok().body(UserDto.IssuanceProgressRes.builder()
				.progress(issuanceProgress.getProgress())
				.status(issuanceProgress.getStatus())
				.build());
	}

	public void saveIssuanceProgress(Long userIdx, IssuanceProgressType progressType, IssuanceStatusType statusType) {
		issuanceProgressRepository.save(IssuanceProgress.builder()
				.userIdx(userIdx)
				.progress(progressType)
				.status(statusType).build());
	}

	public void saveIssuanceProgFailed(Long userIdx, IssuanceProgressType progressType) {
		saveIssuanceProgress(userIdx, progressType, IssuanceStatusType.FAILED);
	}

	public void saveIssuanceProgSuccess(Long userIdx, IssuanceProgressType progressType) {
		saveIssuanceProgress(userIdx, progressType, IssuanceStatusType.SUCCESS);
	}
}
