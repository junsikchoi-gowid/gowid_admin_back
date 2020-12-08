package com.nomadconnection.dapp.redis.service;

import com.nomadconnection.dapp.redis.enums.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate redisTemplate;

	public void putIfAbsent(RedisKey key, Long hashKey, Object hashValue){
		redisTemplate.boundHashOps(key.name()).putIfAbsent(hashKey, hashValue);
	}

	public Object get(RedisKey key, Object value) {
		return redisTemplate.boundHashOps(key.name()).get(value);
	}

	public Object delete(RedisKey key, Object value) {
		return redisTemplate.boundHashOps(key.name()).delete(value);
	}

	public void setExpireMinutes(RedisKey key, int minutes){
		redisTemplate.boundHashOps(key.name()).expire(minutes, TimeUnit.MINUTES);
	}

	public boolean existsKey(RedisKey key, Object hashKey){
		return redisTemplate.boundHashOps(key.name()).hasKey(hashKey);
	}

}
