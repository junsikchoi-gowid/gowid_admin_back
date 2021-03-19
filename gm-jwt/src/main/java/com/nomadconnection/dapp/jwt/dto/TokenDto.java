package com.nomadconnection.dapp.jwt.dto;

import com.nomadconnection.dapp.core.domain.user.Role;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

	@Slf4j
	public enum TokenType {
		JWT_FOR_AUTHENTICATION,
		JWT_FOR_ACCESS,
		JWT_FOR_REFRESH,
		JWT_OUTER_ACCESS;

		public static TokenType from(String name) {
			try {
				return valueOf(name);
			} catch (IllegalArgumentException e) {
				if (log.isErrorEnabled()) {
					log.error("([ from ]) NO CONSTANT WITH THE SPECIFIED NAME, $name='{}'", name);
				}
			}
			return null;
		}
	}

	public enum CustomClaim {
		TOKEN_TYPE,
		IDX,
		IDX_REFERENCE,
		ROLE,
	}

	private TokenType tokenType;

	private String identifier;
	private String issuer;
	private Long idx;
	private Long idxReference;
	private Date issuedAt;
	private Date expiration;
	private String role;

	public TokenDto(Claims claims) {
		issuer = claims.getIssuer();
		identifier = claims.getSubject();
		idx = claims.get(CustomClaim.IDX.name(), Long.class);
		idxReference = claims.get(CustomClaim.IDX_REFERENCE.name(), Long.class);
		role = claims.get(CustomClaim.ROLE.name(), String.class);
		issuedAt = claims.getIssuedAt();
		expiration = claims.getExpiration();
		tokenType = TokenType.from(claims.get(CustomClaim.TOKEN_TYPE.name(), String.class));
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Token {

		@ApiModelProperty("인증토큰")
		private String jwt;

		@ApiModelProperty("발급일시")
		private Date issuedAt;

		@ApiModelProperty("만료일시")
		private Date expiration;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TokenSet {

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		public static class AccountInfo {

			@ApiModelProperty("부여된 권한")
			private List<Role> authorities;

			@ApiModelProperty("법인 매핑")
			private boolean corpMapping;

			@ApiModelProperty("카드 매핑")
			private boolean cardCompanyMapping;

			@ApiModelProperty("인증서 매핑")
			private boolean signMapping;

			@ApiModelProperty("새로고침 매핑")
			private boolean refreshMapping;

			@ApiModelProperty("카드발급정보 idx")
			private Long idxCardIssuance;
		}

		@ApiModelProperty("인증토큰(액세스)")
		private String jwtAccess;

		@ApiModelProperty("인증토큰(갱신)")
		private String jwtRefresh;

		@ApiModelProperty("발급일시")
		private Date issuedAt;

		@ApiModelProperty("만료일시(액세스)")
		private Date jwtAccessExpiration;

		@ApiModelProperty("만료일시(갱신)")
		private Date jwtRefreshExpiration;

		@ApiModelProperty("부가정보")
		private AccountInfo info;
	}
}
