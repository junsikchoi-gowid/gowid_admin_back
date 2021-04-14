package com.nomadconnection.dapp.jwt.authentication;

import com.nomadconnection.dapp.jwt.dto.TokenDto;
import com.nomadconnection.dapp.jwt.exception.AccessTokenNotFoundException;
import com.nomadconnection.dapp.jwt.exception.UnacceptableJwtException;
import com.nomadconnection.dapp.jwt.exception.UnauthorizedException;
import com.nomadconnection.dapp.jwt.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

	private final UserDetailsService service;
	private final JwtService jwt;

	@Value("${quotabook.api-key}")
	private String API_KEY;

	private boolean isAPIKeyAuth(HttpServletRequest request) {
		String apikey = request.getHeader(HttpHeaders.AUTHORIZATION);

		if(Strings.isEmpty(apikey)) {
			return false;
		}

		if(apikey.toLowerCase().startsWith("bearer")) {
			return false;
		}

		if(!API_KEY.equals(apikey)) {
			throw new UnauthorizedException("Invalid api key");
		}
		return true;

	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		if(isAPIKeyAuth(request)) {
			chain.doFilter(request, response);
			return;
		}
		try {
			String bearerToken = request.getHeader(jwt.config().getHeader());
			if (!Strings.isEmpty(bearerToken)) {
				String token = jwt.fromBearerToken(bearerToken).orElseThrow(
						() -> AccessTokenNotFoundException.builder()
								.header(jwt.config().getHeader())
								.bearerToken(bearerToken)
								.build()
				);
				TokenDto dto = jwt.parse(token);
				{
					if (!(TokenDto.TokenType.JWT_FOR_ACCESS.equals(dto.getTokenType())
							|| TokenDto.TokenType.JWT_OUTER_ACCESS.equals(dto.getTokenType()))
						) {
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
		} catch (ExpiredJwtException e){
			SecurityContextHolder.clearContext();
		} catch (UnacceptableJwtException e){
			if (log.isErrorEnabled()) {
				log.error("([ doFilterInternal ]) $error='Could not set user authentication in security context', $exception='{} => {}'",
					e.getClass().getSimpleName(),
					e.getMessage());
			}
			SecurityContextHolder.clearContext();
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("([ doFilterInternal ]) $error='Could not set user authentication in security context', $exception='{} => {}'",
						e.getClass().getSimpleName(),
						e.getMessage());
			}
			SecurityContextHolder.clearContext();
		}
		chain.doFilter(request, response);
	}
}
