package com.nomadconnection.dapp.core.utils;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class EnvUtil implements EnvironmentAware {
	private static Environment environment;

	private final String STAGE = "stage";
	private final String PROD = "prod";

	@Override
	public void setEnvironment(final Environment environment) {
		this.environment = environment;
	}

	public static String[] getActiveProfiles() {
		return environment.getActiveProfiles();
	}

	public boolean isStg(){
		if(Arrays.stream(getActiveProfiles()).anyMatch(STAGE::equals)){
			return true;
		}
		return false;
	}

	public boolean isProd(){
		if(Arrays.stream(getActiveProfiles()).anyMatch(PROD::equals)){
			return true;
		}
		return false;
	}

}
