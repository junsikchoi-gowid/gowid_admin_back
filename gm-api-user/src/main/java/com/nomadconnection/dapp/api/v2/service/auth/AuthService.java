package com.nomadconnection.dapp.api.v2.service.auth;

import com.nomadconnection.dapp.api.enums.VerifyCode;
import com.nomadconnection.dapp.api.exception.ExpiredException;
import com.nomadconnection.dapp.api.exception.MismatchedException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.EmailService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.util.CommonUtil;
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
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
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
		if(!existsEmail(email) && PASSWORD_RESET.equals(type)){
			throw new BadRequestException(ErrorCode.Api.NOT_FOUND, email);
		}

		String code = CommonUtil.get4DigitRandomNumber();
		EmailDto emailDto = buildVerifyEmailDto(email, code, type);

		redisService.putValue(RedisKey.VERIFICATION_CODE, email, code);
		redisService.setExpireSecondsAtValueOps(RedisKey.VERIFICATION_CODE, email, expireTime);
		emailService.send(emailDto);
		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity checkVerificationCode(String email, String code) {
		if(!existsEmail(email)){
			throw new BadRequestException(ErrorCode.Api.NOT_FOUND, email);
		}
		String storedVerifyCode = (String) redisService.getByKey(RedisKey.VERIFICATION_CODE, email);
		if(StringUtils.isEmpty(storedVerifyCode)){
			throw ExpiredException.builder().errorCodeDescriptor(ErrorCode.Authentication.EXPIRED).expiration(LocalDateTime.now()).build();
		}

		if(!code.equals(storedVerifyCode)){
			throw MismatchedException.builder()
				.category(MismatchedException.Category.VERIFICATION_CODE)
				.object(code).build();
		}

		redisService.deleteByKey(RedisKey.VERIFICATION_CODE, email);
		return ResponseEntity.ok().body(
			BusinessResponse.builder().build()
        );
	}

	private boolean existsEmail(String email){
		return userService.isPresentEmail(email);
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
