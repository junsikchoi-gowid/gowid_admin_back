package com.nomadconnection.dapp.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "crownix")
public class CrownixConfig {

	private String stgUrl = "http://10.10.40.176";
	private String prodUrl = "http://10.10.20.63";
	private int port = 8282;
	private String endPoint = "ReportingServer/service";
	private String protocol = "http";

	private boolean enabled;

}
