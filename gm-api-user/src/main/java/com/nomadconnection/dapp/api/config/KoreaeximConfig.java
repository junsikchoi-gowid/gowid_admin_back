package com.nomadconnection.dapp.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "koreaexim")
public class KoreaeximConfig {
	private String domainUrl;
	private String apiKey;
}
