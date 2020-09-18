package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.api.dto.AuthDto;
import com.nomadconnection.dapp.api.dto.UserDto;
import com.nomadconnection.dapp.api.exception.ExpiredException;
import com.nomadconnection.dapp.api.exception.UnauthorizedException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.core.domain.common.IssuanceProgress;
import com.nomadconnection.dapp.core.domain.common.IssuanceProgressType;
import com.nomadconnection.dapp.core.domain.common.IssuanceStatusType;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.common.IssuanceProgressRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.repository.user.VerificationCodeRepository;
import com.nomadconnection.dapp.core.domain.res.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.user.Authority;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.domain.user.VerificationCode;
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
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final EmailConfig config;
	private final ITemplateEngine templateEngine;

	private final JwtService jwt;
	private final JavaMailSenderImpl sender;

	private final UserService serviceUser;
	private final UserRepository repoUser;
	private final ConnectedMngRepository repoConnectedMng;
	private final PasswordEncoder encoder;
	private final VerificationCodeRepository repoVerificationCode;
	private final CardIssuanceInfoRepository repoCardIssuance;
	private final IssuanceProgressRepository issuanceProgressRepository;

	/**
	 * 아이디(이메일) 존재여부 확인
	 *
	 * @param account 아이디(이메일)
	 * @return 아이디(이메일) 존재여부
	 */
	public boolean isPresent(String account) {
		return repoUser.findByAuthentication_EnabledAndEmail(true, account).isPresent();

	}

	/**
	 * 인증번호(4 digits) 확인
	 * <p>
	 * - 확인에 성공하더라도 인증번호를 삭제하지 않음
	 *
	 * @param key  연락처(폰) or 메일주소
	 * @param code 인증번호(4 digits)
	 * @return 존재여부
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean checkVerificationCode(String key, String code, boolean boolDelete) {
		if (!repoVerificationCode.findByVerificationKeyAndCode(key, code).isPresent()) {
			return false;
		}
		if (boolDelete) {
			repoVerificationCode.deleteById(key);
		}
		return true;
	}

	/**
	 * 사용자 계정 찾기
	 *
	 * @param name 이름
	 * @param mdn  연락처(폰)
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
	public void sendPasswordResetEmail(String email) throws UserNotFoundException {
		User user = repoUser.findByAuthentication_EnabledAndEmail(true, email).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(email)
						.build()
		);
		TokenDto.Token token = jwt.issue(email, TokenDto.TokenType.JWT_FOR_AUTHENTICATION, new Date());
		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			helper.setFrom(config.getSender());
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
	 * @param password          비밀번호(신규)
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
		User user = repoUser.findByAuthentication_EnabledAndEmail(true, token.getIdentifier()).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(token.getIdentifier())
						.build()
		);
		user.password(encoder.encode(password));
	}

	/**
	 * 인증토큰 발급
	 * <p>
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
		User user = repoUser.findByAuthentication_EnabledAndEmail(true, dto.getEmail()).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(dto.getEmail())
						.build()
		);

		if (!encoder.matches(dto.getPassword(), user.password())) {
			throw UnauthorizedException.builder()
					.account(dto.getEmail())
					.build();
		}

		boolean corpMapping = !StringUtils.isEmpty(user.corp());
		boolean cardCompanyMapping = !StringUtils.isEmpty(user.cardCompany());

		return jwt.issue(dto.getEmail(), user.authorities(), user.idx(), corpMapping, cardCompanyMapping);
	}

	public TokenDto.TokenSet issueTokenSetOut(AccountDto dto) {
		User user = repoUser.findByAuthentication_EnabledAndEmail(true, dto.getEmail()).orElseThrow(
				() -> UserNotFoundException.builder()
						.email(dto.getEmail())
						.build()
		);

		if(!dto.getPassword().equals("string")){
			throw new RuntimeException("what ~?");
		}

		boolean corpMapping = !StringUtils.isEmpty(user.corp());
		boolean cardCompanyMapping = !StringUtils.isEmpty(user.cardCompany());

		return jwt.issueOut(dto.getEmail(), user.authorities(), user.idx(), corpMapping, cardCompanyMapping);
	}

	/**
	 * 인증토큰 갱신
	 *
	 * @param email 아이디(이메일)
	 * @param jwt   갱신토큰
	 * @return 재발급된 인증토큰 정보 - 인증토큰, 생성일시, 만료일시
	 */
	public TokenDto.Token reissueAccessToken(String email, String jwt) {
		return this.jwt.reissueAccessToken(email, jwt);
	}

	/**
	 * 정보 조회
	 * <p>
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
		Set<Authority> authorities = user.authorities();

		boolean corpMapping = !StringUtils.isEmpty(user.corp());
		boolean cardCompanyMapping = !StringUtils.isEmpty(user.cardCompany());
		boolean signMapping = false;
		if(repoConnectedMng.findByIdxUser(idxUser).size() > 0 ){
			signMapping = true;
		}

		boolean refreshMapping = true;
		if(repoConnectedMng.findRefresh(idxUser) > 0 ){
			refreshMapping = false;
		}

		AtomicReference<Long> idxCardIssuance = new AtomicReference<>(0L);


		repoCardIssuance.findTopByUserAndDisabledFalseOrderByIdxDesc(user).ifPresent(
				cardIssuanceInfo -> { idxCardIssuance.set(cardIssuanceInfo.idx()); }
		);

		return AuthDto.AuthInfo.builder()
				.idx(user.idx())
				.idxCorp(user.corp() != null ? user.corp().idx() : null)
				.email(user.email())
				.name(user.name())
				.mdn(user.mdn())
				.companyCard(!ObjectUtils.isEmpty(user.cardCompany()) ? user.cardCompany().name() : null)
				.corpStatus(!ObjectUtils.isEmpty(user.corp()) ? user.corp().status() : null)
				.info(TokenDto.TokenSet.AccountInfo.builder()
						.authorities(authorities.stream().map(Authority::role).collect(Collectors.toList()))
						.cardCompanyMapping(cardCompanyMapping)
						.corpMapping(corpMapping)
						.signMapping(signMapping)
						.refreshMapping(refreshMapping)
						.idxCardIssuance(idxCardIssuance.get())
						.build())
				.issuanceProgressRes(issuanceProgress(user))
				.build();
	}

	private UserDto.IssuanceProgressRes issuanceProgress(User user) {
		IssuanceProgress issuanceProgress = issuanceProgressRepository.findById(user.idx()).orElse(
				IssuanceProgress.builder()
						.userIdx(user.idx())
						.corpIdx(!ObjectUtils.isEmpty(user.corp()) ? user.corp().idx() : null)
						.progress(IssuanceProgressType.NOT_SIGNED)
						.status(IssuanceStatusType.SUCCESS)
						.build()
		);

		return UserDto.IssuanceProgressRes.builder()
				.progress(issuanceProgress.getProgress())
				.status(issuanceProgress.getStatus())
				.build();
	}

	/**
	 * 인증코드(4 digits) 발송(이메일)
	 *
	 * @param email 수신메일주소
	 * @return true if succeeded, otherwise false
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean sendVerificationCode(String email, String type) {
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
		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.displayName());
			{
				Context context = new Context();
				{
					context.setVariable("verification_code", code);
				}
				helper.setFrom(config.getSender());
				helper.setTo(email);

				if(type.equals("register")){
					helper.setSubject("[Gowid] 회원가입 이메일 인증번호");
					helper.setText(templateEngine.process("signup", context), true);
				}else if(type.equals("password_reset")){
					helper.setSubject("[Gowid] 비밀번호 재설정 이메일 인증번호");
					helper.setText(templateEngine.process("password-init", context), true);
				}else{
					helper.setSubject("[Gowid] 이메일 인증번호");
					helper.setText(templateEngine.process("mail-template", context), true);
				}
			}
		};
		sender.send(preparator);
		return true;
	}

}
