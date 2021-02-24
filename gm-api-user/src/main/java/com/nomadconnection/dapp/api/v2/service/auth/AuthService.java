package com.nomadconnection.dapp.api.v2.service.auth;

import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.api.enums.VerifyCode;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.EmailService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.api.v2.dto.AuthDto;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.EmailDto;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.jwt.dto.TokenDto;
import com.nomadconnection.dapp.jwt.service.JwtService;
import com.nomadconnection.dapp.redis.enums.RedisKey;
import com.nomadconnection.dapp.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.nomadconnection.dapp.api.enums.EmailTemplate.*;
import static com.nomadconnection.dapp.api.enums.VerifyCode.PASSWORD_RESET;
import static com.nomadconnection.dapp.api.enums.VerifyCode.REGISTER;

@Slf4j
@Service("AuthV2Service")
@RequiredArgsConstructor
public class AuthService {

	private final EmailService emailService;
	private final RedisService redisService;
	private final UserService userService;
	private final AuthValidator authValidator;
	private final PasswordEncoder encoder;
	private final JwtService jwt;

	@Value("${auth.email.expire-time}")
	private int expireTime;

	/**
	 * 인증토큰 발급 v2
	 * <p>
	 * - 인증토큰
	 * - 갱신토큰
	 * - 발급일시
	 * - 만료일시(인증토큰)
	 * - 만료일시(갱신토큰)
	 * - 로그인 실패시, 지출관리앱 유저일 경우, 별도 에러코드 리턴
	 *
	 * @param dto 계정정보 - 아이디, 비밀번호
	 * @return 인증토큰(세트) - 인증토큰, 갱신토큰, 발급일시, 만료일시(인증토큰), 만료일시(갱신토큰), 부가정보(권한, ...)
	 */
	public TokenDto.TokenSet issueTokenSet(AccountDto dto) {
		User user = userService.getEnabledUserByEmailIfNotExistError(dto.getEmail());

		if (!encoder.matches(dto.getPassword(), user.password())) {
			throw new BadRequestException(ErrorCode.Api.AUTHENTICATION_FAILURE);
		}

		boolean corpMapping = !StringUtils.isEmpty(user.corp());
		boolean cardCompanyMapping = !StringUtils.isEmpty(user.cardCompany());

		return jwt.issue(dto.getEmail(), user.authorities(), user.idx(), corpMapping, cardCompanyMapping);
	}

	/**
	 * 인증코드(4 digits) 발송(이메일)
	 *
	 * @param email 수신메일주소
	 * @return true if succeeded, otherwise false
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<BusinessResponse> sendVerificationCode(String email, VerifyCode type) {
		if (!authValidator.existsEmail(email) && PASSWORD_RESET.equals(type)) {
			throw new BadRequestException(ErrorCode.Api.NOT_FOUND, email);
		}

		String code = CommonUtil.get4DigitRandomNumber();
		EmailDto emailDto = buildVerifyEmailDto(email, code, type);

		if (redisService.existsByKey(RedisKey.VERIFICATION_CODE, email)) {
			redisService.deleteByKey(RedisKey.VERIFICATION_CODE, email);
		}

		redisService.putValue(RedisKey.VERIFICATION_CODE, email, code);
		redisService.setExpireSecondsAtValueOps(RedisKey.VERIFICATION_CODE, email, expireTime);
		emailService.send(emailDto);
		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<BusinessResponse> checkVerificationCode(String email, String code, VerifyCode verifyType) {
		String storedVerifyCode = (String) redisService.getByKey(RedisKey.VERIFICATION_CODE, email);

		authValidator.expiredVerifyCodeThrowException(storedVerifyCode);
		authValidator.mismatchedVerifyCodeThrowException(code, storedVerifyCode);

		if (REGISTER.equals(verifyType)) {
			redisService.deleteByKey(RedisKey.VERIFICATION_CODE, email);
		}
		return ResponseEntity.ok().body(
				BusinessResponse.builder().build()
        );
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> changePasswordBeforeLogin(AuthDto.PasswordBeforeLogin dto) {
		String email = dto.getEmail();
		String code = dto.getCode();
		String newPassword = dto.getNewPassword();

		authValidator.notExistsEmailThrowException(dto.getEmail());
		String storedVerifyCode = (String)redisService.getByKey(RedisKey.VERIFICATION_CODE, email);
		authValidator.expiredVerifyCodeThrowException(storedVerifyCode);
		authValidator.mismatchedVerifyCodeThrowException(code, storedVerifyCode);

		if(code.equals(storedVerifyCode)){
			User user = userService.findByEmail(email);
			user.password(authValidator.encodePassword(newPassword));

			redisService.deleteByKey(RedisKey.VERIFICATION_CODE, email);
		}

		return ResponseEntity.ok().body(BusinessResponse.builder()
			.normal(BusinessResponse.Normal.builder().build())
			.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> changePasswordAfterLogin(Long idxUser, AuthDto.PasswordAfterLogin dto) {
		String oldPassword = dto.getOldPassword();
		String newPassword = dto.getNewPassword();

		User user = userService.getUser(idxUser);
		authValidator.matchedPassword(oldPassword, user.password()); // 현재패스워드 검사
		user.password(authValidator.encodePassword(newPassword));

		return ResponseEntity.ok().body(BusinessResponse.builder()
			.normal(BusinessResponse.Normal.builder()
				.build())
			.data(user)
			.build());
	}

	private EmailDto buildVerifyEmailDto(String email, String code, VerifyCode type){
		final String VERIFICATION_CODE = "verification_code";
		Map<String, Object> context = new HashMap<>();
		context.put(VERIFICATION_CODE, code);
		String template = VERIFY_CODE_DEFAULT.getTemplate();
		String subject = VERIFY_CODE_DEFAULT.getSubject();

		if(REGISTER.equals(type)){
			subject = VERIFY_CODE_REGISTER.getSubject();
			template = VERIFY_CODE_REGISTER.getTemplate();
		} else if(PASSWORD_RESET.equals(type)){
			subject = VERIFY_CODE_PASSWORD_RESET.getSubject();
			template = VERIFY_CODE_PASSWORD_RESET.getTemplate();
		}

		return EmailDto.builder()
					.subject(subject).template(template).context(context).receiver(email)
					.build();
	}

}
