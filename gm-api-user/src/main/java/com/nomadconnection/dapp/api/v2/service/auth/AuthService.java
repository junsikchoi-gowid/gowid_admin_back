package com.nomadconnection.dapp.api.v2.service.auth;

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
import com.nomadconnection.dapp.redis.enums.RedisKey;
import com.nomadconnection.dapp.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Value("${auth.email.expire-time}")
	private int expireTime;

	/**
	 * 인증코드(4 digits) 발송(이메일)
	 *
	 * @param email 수신메일주소
	 * @return true if succeeded, otherwise false
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity sendVerificationCode(String email, VerifyCode type) {
		if(!authValidator.existsEmail(email) && PASSWORD_RESET.equals(type)){
			throw new BadRequestException(ErrorCode.Api.NOT_FOUND, email);
		}

		String code = CommonUtil.get4DigitRandomNumber();
		EmailDto emailDto = buildVerifyEmailDto(email, code, type);

		if(redisService.existsByKey(RedisKey.VERIFICATION_CODE, email)){
			redisService.deleteByKey(RedisKey.VERIFICATION_CODE, email);
		}

		redisService.putValue(RedisKey.VERIFICATION_CODE, email, code);
		redisService.setExpireSecondsAtValueOps(RedisKey.VERIFICATION_CODE, email, expireTime);
		emailService.send(emailDto);
		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity checkVerificationCode(String email, String code) {
		String storedVerifyCode = (String) redisService.getByKey(RedisKey.VERIFICATION_CODE, email);

		authValidator.expiredVerifyCodeThrowException(storedVerifyCode);
		authValidator.mismatchedVerifyCodeThrowException(code, storedVerifyCode);

		redisService.deleteByKey(RedisKey.VERIFICATION_CODE, email);
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
					.subject(subject).template(template).context(context).to(email)
					.build();
	}

}
