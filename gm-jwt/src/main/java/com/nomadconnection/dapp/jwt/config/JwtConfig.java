package com.nomadconnection.dapp.jwt.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Getter
@Configuration
@ConfigurationProperties(prefix = "authentication.jwt")
@SuppressWarnings({"unused", "WeakerAccess", "SpellCheckingInspection"})
public class JwtConfig {

	private String issuer = "nomad";
	private String header = HttpHeaders.AUTHORIZATION;
	private String base64SecretKey = "MjAxOSBNeUNhcmQgSldUIFNlY3JldCwgTk9NQURDT05ORUNUSU9OLCBTSC9ZQi9KSC9DQy9DQy9QQyBHTyAhIQ==";

	private Validity validity = new Validity();

	@Getter
	public static class Validity {
		private Long defaultTokenValidity = 5 * 60 * 1000L; // 5 Minutes
		private Long accessTokenValidity = 2 * 60 * 60 * 1000L; // 2 Hours
		private Long refreshTokenValidity = 24 * 60 * 60 * 1000L; // 1 Days
		private Long outConnectTokenValidity = 30 * 24 * 60 * 60 * 1000L; // 30 Days
	}
}