package com.nomadconnection.dapp.core.utils;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class EnvUtil implements EnvironmentAware {
	private static Environment environment;

	private final String DEV = "dev";
	private final String STAGE = "stage";
	private final String PROD = "prod";

	@Override
	public void setEnvironment(Environment environment) {
		EnvUtil.environment = environment;
	}

	public static String[] getActiveProfiles() {
		return environment.getActiveProfiles();
	}

	public boolean isDev(){
		return Arrays.stream(getActiveProfiles()).anyMatch(DEV::equals);
	}

	public boolean isStg(){
		return Arrays.stream(getActiveProfiles()).anyMatch(STAGE::equals);
	}

	public boolean isProd(){
		return Arrays.stream(getActiveProfiles()).anyMatch(PROD::equals);
	}

}
