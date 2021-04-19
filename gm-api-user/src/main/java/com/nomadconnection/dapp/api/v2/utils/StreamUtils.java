package com.nomadconnection.dapp.api.v2.utils;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

public class StreamUtils {

	public static <T> boolean in(List<T> list, T target){
		if(CollectionUtils.isEmpty(list) || ObjectUtils.isEmpty(target)){
			return false;
		}

		return list.stream().anyMatch(target::equals);
	}

}
