package com.nomadconnection.dapp.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "slack")
public class SlackNotiConfig {

	private String url;
	private Boolean enable;
}
