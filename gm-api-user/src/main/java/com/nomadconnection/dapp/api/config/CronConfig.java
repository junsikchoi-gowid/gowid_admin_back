package com.nomadconnection.dapp.api.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.cron")
public class CronConfig {

	private String enabled;
	private String time;
	private String endtime;
}
