package com.nomadconnection.dapp.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "expense")
public class ExpenseConfig {
	private String domainUrl;
	private String userUrl;
	private String statusUrl;
	private String apiKey;
	private String accessKey;
}
