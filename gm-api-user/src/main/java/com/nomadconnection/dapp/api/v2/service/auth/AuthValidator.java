package com.nomadconnection.dapp.api.v2.service.auth;

import com.nomadconnection.dapp.api.exception.ExpiredException;
import com.nomadconnection.dapp.api.exception.MismatchedException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthValidator {

	private final PasswordEncoder encoder;
	private final UserService userService;

	public boolean matchedPassword(String oldPassword, String newPassword){
		boolean matched = encoder.matches(oldPassword, newPassword);
		if (!matched) {
			throw MismatchedException.builder().category(MismatchedException.Category.PASSWORD).build();
		}
		return true;
	}

	public void notExistsEmailThrowException(String email){
		if(!existsEmail(email)){
			throw new BadRequestException(ErrorCode.Api.NOT_FOUND, email);
		}
	}

	public void expiredVerifyCodeThrowException(String storedVerifyCode){
		if(StringUtils.isEmpty(storedVerifyCode)){
			throw ExpiredException.builder().errorCodeDescriptor(ErrorCode.Authentication.EXPIRED).expiration(LocalDateTime.now()).build();
		}
	}

	public void mismatchedVerifyCodeThrowException(String targetCode, String storedVerifyCode){
		if(!targetCode.equals(storedVerifyCode)){
			throw MismatchedException.builder()
				.category(MismatchedException.Category.VERIFICATION_CODE)
				.object(targetCode).build();
		}
	}

	public boolean existsEmail(String email){
		return userService.isPresentEmail(email);
	}

	public String encodePassword(String password){
		return encoder.encode(password);
	}
}
