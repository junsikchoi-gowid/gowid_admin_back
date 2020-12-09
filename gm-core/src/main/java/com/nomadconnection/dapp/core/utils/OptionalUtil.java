package com.nomadconnection.dapp.core.utils;

import java.util.Optional;

public class OptionalUtil {

	public static String getOrEmptyString(String target){
		return Optional.ofNullable(target).orElse("");
	}

	public static <T> boolean isNotNull(T target){
		return Optional.ofNullable(target).isPresent();
	}

}
