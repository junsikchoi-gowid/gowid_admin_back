package com.nomadconnection.dapp.jwt.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.core.dto.response.ErrorCodeDescriptor;
import com.nomadconnection.dapp.core.dto.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objmpr;

	public static class ErrorCode {
		enum Authentication implements ErrorCodeDescriptor {
			INVALID_TOKEN_USED,
		}
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		response.sendError(response.SC_UNAUTHORIZED,
				objmpr.writerWithDefaultPrettyPrinter().writeValueAsString(
						ErrorResponse.from(ErrorCode.Authentication.INVALID_TOKEN_USED)
				)
		);
	}
}
