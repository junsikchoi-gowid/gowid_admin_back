package com.nomadconnection.dapp.jwt.service;

import com.nomadconnection.dapp.core.domain.user.Authority;
import com.nomadconnection.dapp.jwt.config.JwtConfig;
import com.nomadconnection.dapp.jwt.dto.TokenDto;
import com.nomadconnection.dapp.jwt.exception.JwtSubjectMismatchedException;
import com.nomadconnection.dapp.jwt.exception.UnacceptableJwtException;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

	@Getter
	@Accessors(fluent = true)
	private final JwtConfig config;

	public Optional<String> fromBearerToken(String bearerToken) {
		if (!StringUtils.isEmpty(bearerToken)) {
			Matcher matcher = Pattern.compile("^(?i)bearer (?<jwt>.*)$").matcher(bearerToken);
			if (matcher.find()) {
				return Optional.of(matcher.group(matcher.groupCount()));
			}
		}
		return Optional.empty();
	}

	public TokenDto parse(String jwt) throws UnacceptableJwtException {
		try {
			return new TokenDto(
					Jwts.parser()
							.setSigningKey(config.getBase64SecretKey())
							.parseClaimsJws(jwt)
							.getBody()
			);
		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
			if (log.isErrorEnabled()) {
				log.error("([ parse ]) $error='failed to parse, jwt', $jwt='{}', $exception='{} => {}'",
						jwt,
						e.getClass().getSimpleName(),
						e.getMessage());
			}
			throw UnacceptableJwtException.builder().jwt(jwt).build();
		}
	}

	public TokenDto.TokenSet issue(String identifier, Set<Authority> authorities, Long idx, boolean corpMapping, boolean cardCompanyMapping) {
		Date now = new Date();
		List<TokenDto.Token> jwts = Arrays.asList(
				issue(identifier, TokenDto.TokenType.JWT_FOR_ACCESS, now, idx),
				issue(identifier, TokenDto.TokenType.JWT_FOR_REFRESH, now, idx)
		);

		return TokenDto.TokenSet.builder()
				.jwtAccess(jwts.get(0).getJwt())
				.jwtRefresh(jwts.get(1).getJwt())
				.issuedAt(now)
				.jwtAccessExpiration(jwts.get(0).getExpiration())
				.jwtRefreshExpiration(jwts.get(1).getExpiration())
				.info(TokenDto.TokenSet.AccountInfo.builder()
						.authorities(authorities.stream().map(Authority::role).collect(Collectors.toList()))
						.cardCompanyMapping(cardCompanyMapping)
						.corpMapping(corpMapping)
						.build())
				.build();
	}

	public TokenDto.TokenSet issueOut(String identifier, Set<Authority> authorities, Long idx, boolean corpMapping, boolean cardCompanyMapping) {
		Date now = new Date();
		List<TokenDto.Token> jwts = Arrays.asList(
				issue(identifier, TokenDto.TokenType.JWT_OUTER_ACCESS, now, idx),
				issue(identifier, TokenDto.TokenType.JWT_FOR_REFRESH, now, idx)
		);

		return TokenDto.TokenSet.builder()
				.jwtAccess(jwts.get(0).getJwt())
				.jwtRefresh(jwts.get(1).getJwt())
				.issuedAt(now)
				.jwtAccessExpiration(jwts.get(0).getExpiration())
				.jwtRefreshExpiration(jwts.get(1).getExpiration())
				.info(TokenDto.TokenSet.AccountInfo.builder()
						.authorities(authorities.stream().map(Authority::role).collect(Collectors.toList()))
						.cardCompanyMapping(cardCompanyMapping)
						.corpMapping(corpMapping)
						.build())
				.build();
	}

	public TokenDto.Token issue(String identifier, TokenDto.TokenType tokenType, Date now) {
		return issue(identifier, tokenType, now, null, null);
	}

	public TokenDto.Token issue(String identifier, TokenDto.TokenType tokenType, Date now, Long idx) {
		return issue(identifier, tokenType, now, idx, null);
	}

	public TokenDto.Token issue(String identifier, TokenDto.TokenType tokenType, Date now, Long idx, Long idxReference) {
		Date expiration;
		{
			switch (tokenType) {
				case JWT_FOR_ACCESS:
					expiration = new Date(now.getTime() + config.getValidity().getAccessTokenValidity());
					break;
				case JWT_FOR_REFRESH:
					expiration = new Date(now.getTime() + config.getValidity().getRefreshTokenValidity());
					break;
				case JWT_OUTER_ACCESS:
					expiration = new Date(now.getTime() + config.getValidity().getOutConnectTokenValidity());
					break;
				default:
					expiration = new Date(now.getTime() + config.getValidity().getDefaultTokenValidity());
					break;
			}
		}
		return TokenDto.Token.builder()
				.jwt(Jwts.builder()
						.setSubject(identifier)
						.setIssuer(config.getIssuer())
						.setIssuedAt(now)
						.setExpiration(expiration)
						.claim(TokenDto.CustomClaim.TOKEN_TYPE.name(), tokenType)
						.claim(TokenDto.CustomClaim.IDX.name(), idx)
						.claim(TokenDto.CustomClaim.IDX_REFERENCE.name(), idxReference)
						.signWith(SignatureAlgorithm.HS512, config.getBase64SecretKey())
						.compact())
				.issuedAt(now)
				.expiration(expiration)
				.build();
	}

	public TokenDto.Token issue(TokenDto dto) {
		if (log.isDebugEnabled()) {
			log.debug("([ issue ]) $dto='{}'", dto);
		}
		return TokenDto.Token.builder()
				.jwt(Jwts.builder()
						.setSubject(dto.getIdentifier())
						.setIssuer(config.getIssuer())
						.setIssuedAt(dto.getIssuedAt())
						.setExpiration(dto.getExpiration())
						.claim(TokenDto.CustomClaim.TOKEN_TYPE.name(), dto.getTokenType())
						.claim(TokenDto.CustomClaim.IDX.name(), dto.getIdx())
						.claim(TokenDto.CustomClaim.IDX_REFERENCE.name(), dto.getIdxReference())
						.signWith(SignatureAlgorithm.HS512, config.getBase64SecretKey())
						.compact())
				.issuedAt(dto.getIssuedAt())
				.expiration(dto.getExpiration())
				.build();
	}

	public TokenDto.Token reissueAccessToken(String identifier, String jwt) {
		TokenDto dto = parse(jwt);
		{
			if (!TokenDto.TokenType.JWT_FOR_REFRESH.equals(dto.getTokenType())) {
				throw UnacceptableJwtException.builder()
						.jwt(jwt)
						.tokenType(dto.getTokenType())
						.expectedTokenType(TokenDto.TokenType.JWT_FOR_REFRESH)
						.build();
			}
			if (!identifier.equals(dto.getIdentifier())) {
				throw JwtSubjectMismatchedException.builder()
						.jwt(jwt)
						.subject(identifier)
						.build();
			}
		}
		Date now, expiration;
		{
			now = new Date();
			expiration = new Date(now.getTime() + config.getValidity().getAccessTokenValidity());
		}
		return issue(TokenDto.builder()
				.identifier(identifier)
				.issuedAt(now)
				.idx(dto.getIdx())
				.idxReference(dto.getIdxReference())
				.tokenType(TokenDto.TokenType.JWT_FOR_ACCESS)
				.expiration(expiration)
				.build());
	}
}
