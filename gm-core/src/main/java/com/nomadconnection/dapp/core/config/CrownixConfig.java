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

	private String stgUrl = "http://ec2-3-34-131-218.ap-northeast-2.compute.amazonaws.com";
	private String prodUrl;	//TODO: set PROD URL
	private int port = 8282;
	private String endPoint = "ReportingServer/service";
	private String protocol = "http";

}
