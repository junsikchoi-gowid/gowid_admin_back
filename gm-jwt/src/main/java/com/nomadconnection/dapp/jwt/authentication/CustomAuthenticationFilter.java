package com.nomadconnection.dapp.jwt.authentication;

import com.nomadconnection.dapp.jwt.dto.TokenDto;
import com.nomadconnection.dapp.jwt.exception.AccessTokenNotFoundException;
import com.nomadconnection.dapp.jwt.exception.UnacceptableJwtException;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CustomAuthenticationFilter extends OncePerRequestFilter {

	private final UserDetailsService service;
	private final JwtService jwt;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		try {
			String bearerToken = request.getHeader(jwt.config().getHeader());
			{
				if (log.isDebugEnabled()) {
					log.debug("([ doFilterInternal ]) $uri='{}', $bearer='{}'", request.getRequestURI(), bearerToken);
				}
			}
			if (!Strings.isEmpty(bearerToken)) {
				if (log.isDebugEnabled()) {
					log.debug("([ doFilterInternal ]) $uri='{}', $bearer='{}'", request.getRequestURI(), bearerToken);
				}
				String token = jwt.fromBearerToken(bearerToken).orElseThrow(
						() -> AccessTokenNotFoundException.builder()
								.header(jwt.config().getHeader())
								.bearerToken(bearerToken)
								.build()
				);
				TokenDto dto = jwt.parse(token);
				{
					if (!TokenDto.TokenType.JWT_FOR_ACCESS.equals(dto.getTokenType())) {
						if (log.isDebugEnabled()) {
							log.debug("([ doFilterInternal ]) INVALID JWT TYPE( {} ), $jwt='{}'", dto.getTokenType(), token);
						}
						throw UnacceptableJwtException.builder()
								.jwt(token)
								.tokenType(dto.getTokenType())
								.expectedTokenType(TokenDto.TokenType.JWT_FOR_ACCESS)
								.build();
					}
				}
				UserDetails user = service.loadUserByUsername(dto.getIdentifier());

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				{
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				}
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("([ doFilterInternal ]) $error='Could not set user authentication in security context', $exception='{} => {}'",
						e.getClass().getSimpleName(),
						e.getMessage(),
						e);
			}
			SecurityContextHolder.clearContext();
		}
		chain.doFilter(request, response);
	}
}
