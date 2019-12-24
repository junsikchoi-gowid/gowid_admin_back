package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.UserDto;
import com.nomadconnection.dapp.api.exception.*;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.embed.Authentication;
import com.nomadconnection.dapp.core.domain.repository.AuthorityRepository;
import com.nomadconnection.dapp.core.domain.repository.DeptRepository;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import com.nomadconnection.dapp.core.domain.repository.VerificationCodeRepository;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final JwtService jwt;
	private final JavaMailSenderImpl sender;

	private final PasswordEncoder encoder;
	private final UserRepository repo;
	private final DeptRepository repoDept;
	private final AuthorityRepository repoAuthority;
	private final VerificationCodeRepository repoVerificationCode;

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
		if (repo.findByEmail(dto.getEmail()).isPresent()) {
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
			User user = repo.findByEmail(dto.getEmail()).orElseThrow(
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


}
