package com.nomadconnection.dapp.resx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Configuration
@ConfigurationProperties(prefix = "resx")
@SuppressWarnings("unused")
public class ResourceConfig {

	private String root;
	private String resxUriPrefix;

//	public Path getAbsoluteResxRootPath() {
//		return Paths.get(root).toAbsolutePath().normalize();
//	}
}
