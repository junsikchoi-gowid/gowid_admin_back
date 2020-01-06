package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.api.dto.AuthDto;
import com.nomadconnection.dapp.api.exception.BusinessException;
import com.nomadconnection.dapp.api.exception.ExpiredException;
import com.nomadconnection.dapp.api.exception.UnauthorizedException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.helper.EmailValidator;
import com.nomadconnection.dapp.api.helper.MdnValidator;
import com.nomadconnection.dapp.core.domain.User;
import com.nomadconnection.dapp.core.domain.VerificationCode;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import com.nomadconnection.dapp.core.domain.repository.VerificationCodeRepository;
import com.nomadconnection.dapp.jwt.dto.TokenDto;
import com.nomadconnection.dapp.jwt.exception.UnacceptableJwtException;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AuthService {

	private final JwtService jwt;
	private final JavaMailSenderImpl sender;

	private final UserService serviceUser;
	private final UserRepository repoUser;
	private final PasswordEncoder encoder;
	private final VerificationCodeRepository repoVerificationCode;

	/**
	 * 아이디(이메일) 존재여부 확인
	 * @param account 아이디(이메일)
	 * @return 아이디(이메일) 존재여부
	 */
	public boolean isPresent(String account) {
		return repoUser.findByEmail(account).isPresent();
	}

	@Transactional(rollbackFor = Exception.class)
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean sendVerificationCode(String key) {
		if (MdnValidator.isValid(key)) {
			return sendMdnVerificationCode(key);
		}
		if (EmailValidator.isValid(key)) {
			return sendEmailVerificationCode(key);
		}
		return false;
	}

	@Transactional(rollbackFor = Exception.class)
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean sendVerificationCodeMail(String key) {
		if (MdnValidator.isValid(key)) {
			return sendMdnVerificationCode(key);
		}
		if (EmailValidator.isValid(key)) {
			return sendEmailVerificationCode(key);
		}
		return false;
	}

	private boolean sendMdnVerificationCode(String mdn) {
		//
		//	todo: send mdn verification code
		//
		return true;
	}

	/**
	 * 인증번호(4 digits, EMAIL) 발송
	 *
	 * @param email 수신메일주소 Password 비밀번호
	 */
	private boolean sendEmailVerificationCode(String email) {
		String code = String.format("%04d", new Random().nextInt(10000));

		try {
			repoVerificationCode.save(VerificationCode.builder()
					.verificationKey(email)
					.code(code)
					.build());
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("([ sendVerificationCode ]) REPOSITORY.SAVE ERROR, $email='{}'", email, e);
			}
			return false;
		}
		final MimeMessagePreparator preparator = mimeMessage -> {
			final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			helper.setFrom("MyCard <service@popsoda.io>");
			helper.setTo(email);
			helper.setSubject("[MyCard] 인증코드");
			helper.setText("Verification Code: " + code, false);
		};
		sender.send(preparator);
		return true;
	}

	/**
	 * 인증번호(4 digits) 확인
	 *
	 * - 확인에 성공하더라도 인증번호를 삭제하지 않음
	 *
	 * @param key 연락처(폰) or 메일주소
	 * @param code 인증번호(4 digits)
	 * @return 존재여부
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean checkVerificationCode(String key, String code) {
		if (!repoVerificationCode.findByVerificationKeyAndCode(key, code).isPresent()) {
			return false;
		}
		repoVerificationCode.deleteById(key);
		return true;
	}

	/**
	 * 사용자 계정 찾기
	 *
	 * @param name 이름
	 * @param mdn 연락처(폰)
	 * @return 계정 정보
	 */
	@Transactional
	public List<String> findAccount(String name, String mdn) {
		return repoUser.findByNameAndMdn(name, mdn)
				.map(User::email)
				.map(email -> email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*"))
				.collect(Collectors.toList());
	}

	/**
	 * 비밀번호 재설정 - 이메일 발송
	 *
	 * @param email 이메일주소
	 */
	public void sendPasswordResetEmail(String email) throws UserNotFoundException  {
		User user = repoUser.findByEmail(email).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(email)
						.build()
		);
		final TokenDto.Token token = jwt.issue(email, TokenDto.TokenType.JWT_FOR_AUTHENTICATION, new Date());
		final MimeMessagePreparator preparator = mimeMessage -> {
			final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			helper.setFrom("MyCard <service@popsoda.io>");
			helper.setTo(email);
			helper.setSubject("[MyCard] 비밀번호 재설정");
			helper.setText("인증키: " + token.getJwt(), false);
		};
		sender.send(preparator);
	}

	/**
	 * 비밀번호 재설정 - 새 비밀번호 설정
	 *
	 * @param authenticationKey 인증키
	 * @param password 비밀번호(신규)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void resetPassword(String authenticationKey, String password) {
		TokenDto token = jwt.parse(authenticationKey);
		{
			if (!TokenDto.TokenType.JWT_FOR_AUTHENTICATION.equals(token.getTokenType())) {
				throw UnacceptableJwtException.builder()
						.jwt(authenticationKey)
						.tokenType(token.getTokenType())
						.expectedTokenType(TokenDto.TokenType.JWT_FOR_AUTHENTICATION)
						.build();
			}
		}
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiration = LocalDateTime.ofInstant(token.getExpiration().toInstant(), ZoneId.systemDefault());
		{
			if (!now.isBefore(expiration)) {
				throw ExpiredException.builder()
						.now(now)
						.expiration(expiration)
						.build();
			}
		}
		User user = repoUser.findByEmail(token.getIdentifier()).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(token.getIdentifier())
						.build()
		);
		user.password(encoder.encode(password));
	}

	/**
	 * 인증토큰 발급
	 *
	 * - 인증토큰
	 * - 갱신토큰
	 * - 발급일시
	 * - 만료일시(인증토큰)
	 * - 만료일시(갱신토큰)
	 *
	 * @param dto 계정정보 - 아이디, 비밀번호
	 * @return 인증토큰(세트) - 인증토큰, 갱신토큰, 발급일시, 만료일시(인증토큰), 만료일시(갱신토큰), 부가정보(권한, ...)
	 */
	public TokenDto.TokenSet issueTokenSet(AccountDto dto) {
		User user = repoUser.findByEmail(dto.getEmail()).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(dto.getEmail())
						.build()
		);
		if (!encoder.matches(dto.getPassword(), user.password())) {
			throw UnauthorizedException.builder()
					.account(dto.getEmail())
					.build();
		}
		return jwt.issue(dto.getEmail(), user.authorities(), user.idx());
	}

	/**
	 * 인증토큰 갱신
	 *
	 * @param email 아이디(이메일)
	 * @param jwt 갱신토큰
	 * @return 재발급된 인증토큰 정보 - 인증토큰, 생성일시, 만료일시
	 */
	public TokenDto.Token reissueAccessToken(String email, String jwt) {
		return this.jwt.reissueAccessToken(email, jwt);
	}

	/**
	 * 정보 조회
	 *
	 * - 사용자 정보
	 * - 소속 법인 정보
	 * - 각 상태 정보
	 *
	 * @param idxUser 식별자(사용자)
	 * @return 정보
	 */
	@Transactional
	public AuthDto.AuthInfo info(Long idxUser) {
		User user = serviceUser.getUser(idxUser);
		return AuthDto.AuthInfo.builder()
				.idx(user.idx())
				.idxCorp(user.corp() != null ? user.corp().idx() : null)
				.idxCard(user.card() != null ? user.card().idx() : null)
//				.cards(user.cards().stream().map(CardDto.CardBasicInfo::from).collect(Collectors.toList()))
				.recipientAddress(user.corp() != null ? user.corp().recipientAddress() : null)
				.email(user.email())
				.name(user.name())
				.mdn(user.mdn())
				.corpStatus(user.corp() != null ? user.corp().status() : null)
				.build();
	}

	/**
	 * 인증번호(4 digits, EMAIL) 발송
	 *
	 * @param email 수신메일주소 Password 비밀번호
	 */
	public boolean sendEmailVerificationCode(AccountDto dto) {
		String code = String.format("%04d", new Random().nextInt(10000));

		User user = repoUser.findByEmail(dto.getEmail()).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(dto.getEmail())
						.build()
		);
		if (!encoder.matches(dto.getPassword(), user.password())) {
			throw UnauthorizedException.builder()
					.account(dto.getEmail())
					.build();
		}

		try {
			repoVerificationCode.save(VerificationCode.builder()
					.verificationKey(user.email())
					.code(code)
					.build());
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("([ sendVerificationCode ]) REPOSITORY.SAVE ERROR, $email='{}'", user.email(), e);
			}
			return false;
		}
		final MimeMessagePreparator preparator = mimeMessage -> {
			final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			helper.setFrom("MyCard <service@popsoda.io>");
			helper.setTo(user.email());
			helper.setSubject("[MyCard] 인증코드");
			helper.setText("Verification Code: " + code, false);
		};
		sender.send(preparator);
		return true;
	}
}
