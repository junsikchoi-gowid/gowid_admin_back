package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.api.dto.ConsentDto;
import com.nomadconnection.dapp.api.dto.UserDto;
import com.nomadconnection.dapp.api.exception.*;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.embed.Authentication;
import com.nomadconnection.dapp.core.domain.embed.BankAccount;
import com.nomadconnection.dapp.core.domain.repository.*;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.jwt.dto.TokenDto;
import com.nomadconnection.dapp.jwt.service.JwtService;
import com.nomadconnection.dapp.resx.config.ResourceConfig;
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

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final JavaMailSenderImpl sender;
	private final PasswordEncoder encoder;
	private final UserRepository repo;
	private final ConsentRepository repoConsent;
	private final CorpRepository repoCorp;
	private final DeptRepository repoDept;
	private final AuthorityRepository repoAuthority;
	private final VerificationCodeRepository repoVerificationCode;

	private final JwtService jwt;
	private final ResourceConfig configResx;

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
		final MimeMessagePreparator preparator = mimeMessage -> {
			final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			helper.setFrom("MyCard <service@popsoda.io>");
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
	public ResponseEntity registerUserUpdate(UserDto.registerUserUpdate dto, Long idx) {
		// validation start
		// mail check
		if(repo.findByIdxNotAndEmail(idx, dto.getEmail()).isPresent()){
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
	public ResponseEntity registerUserPasswordUpdate(UserDto.registerUserPasswordUpdate dto, Long idx) {

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

		List<Long> listIdx = getConsentIdxList(dto.getConsents());

		//	마스터 등록 (권한 설정 필요)
		User user = repo.save(User.builder()
				.consent(true)
				.email(userDto.getEmail())
				.password(encoder.encode(userDto.getPassword()))
				.name(userDto.getName())
				.mdn(userDto.getMdn())
				.authentication(new Authentication())
				.authorities(Collections.singleton(
						repoAuthority.findByRole(Role.ROLE_MASTER).orElseThrow(
								() -> new RuntimeException("ROLE_MASTER NOT FOUND")
						)))
				.consents(repoConsent.findByIdxIn(listIdx))
				.build());
		// user info - end

		// 이용약관 매핑
		for(ConsentDto.RegDto regDto : dto.getConsents()) {
			repoConsent.updateConsentMapping(regDto.status, user.idx(), regDto.idxConsent);
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
	public ResponseEntity registerBrandCorp(Long idxUser, UserDto.RegisterBrandCorp dto) {
		//	중복체크
		if (repoCorp.findByBizRegNo(dto.getBizRegNo()).isPresent()) {
			if (log.isDebugEnabled()) {
				log.debug("([ registerBrandCorp ]) registerBrandCorp ALREADY EXIST, $idxUser='{}', $getBizRegNo='{}'", idxUser, dto.getBizRegNo());
			}
			throw AlreadyExistException.builder()
					.resource(dto.getBizRegNo())
					.build();
		}

		//	사용자 조회
		User user = getUser(idxUser);

		//	법인정보 저장(상태: 대기)
		Corp corp = repoCorp.save(Corp.builder()
				.user(user)
				.name(dto.getCorpName())
				.bizRegNo(dto.getBizRegNo())
				.reqCreditLimit(dto.getReqCreditLimit())
//				.bankAccount(BankAccount.builder()
//						.bankAccount(dto.getBankAccount().getAccount())
//						.bankAccountHolder(dto.getBankAccount().getAccountHolder())
//						.build())
				.status(CorpStatus.PENDING)
				.build());

		//	사용자-법인 매핑
		repo.save(user.corp(corp));

		//	주주명부 저장경로
		Path path = getResxStockholdersListPath(corp.idx());

		//	법인정보 갱신(주주명부)
//		corp.setResxStockholdersList(CorpStockholdersListResx.builder()
//				.resxStockholdersListPath(path.toString())
//				.resxStockholdersListFilenameOrigin(dto.getResxShareholderList().getOriginalFilename())
//				.resxStockholdersListSize(dto.getResxShareholderList().getSize())
//				.build());

		// 주주명부 저장
//		serviceResx.save(dto.getResxShareholderList(), path, true);

		//	fixme: dummy data - credit limit check
		Long creditLimit = dto.getReqCreditLimit();

		//	법인정보 갱신(상태: 승인/거절, 법인한도)
		corp.creditLimit(creditLimit);
		corp.status(CorpStatus.APPROVED);
		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}


	public TokenDto.TokenSet issueTokenSet(AccountDto dto) {
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

		boolean corpMapping = StringUtils.isEmpty(user.corp())? true: false;
		boolean cardCompanyMapping = StringUtils.isEmpty(user.cardCompany())? true: false;

		return jwt.issue(dto.getEmail(), user.authorities(), user.idx(), corpMapping, cardCompanyMapping);
	}

	public Path getResxStockholdersListPath(Long idxCorp) {
		return Paths.get(configResx.getRoot(), ResxCategory.RESX_CORP_SHAREHOLDERS_LIST.name(), idxCorp.toString()).toAbsolutePath().normalize();
	}
}
