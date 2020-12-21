package com.nomadconnection.dapp.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "quotabook")
public class QuotabookConfig {
	private String domainUrl;
	private String stakeholdersUrl;
	private String shareClassesUrl;
	private String roundingUrl;
}
