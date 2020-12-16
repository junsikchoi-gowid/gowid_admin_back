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

	//HashOps

	public void putHash(RedisKey mainKey, Object hashKey, Object hashValue){
		redisTemplate.boundHashOps(mainKey.name()).putIfAbsent(hashKey, hashValue);
	}

	public Object getByHashKey(RedisKey mainKey, Object hashKey) {
		return redisTemplate.boundHashOps(mainKey.name()).get(hashKey);
	}

	public Object deleteByHashKey(RedisKey mainKey, Object hashKey) {
		return redisTemplate.boundHashOps(mainKey.name()).delete(hashKey);
	}

	public void setExpireMinutesAtHashOps(RedisKey mainKey, int seconds){
		redisTemplate.boundHashOps(mainKey.name()).expire(seconds, TimeUnit.SECONDS);
	}

	public boolean existsByHashKey(RedisKey mainKey, Object hashKey){
		return redisTemplate.boundHashOps(mainKey.name()).hasKey(hashKey);
	}

	//ValueOps

	public void putValue(RedisKey mainKey, Object subKey, Object value){
		String redisKey = getRedisKey(mainKey, subKey);
		redisTemplate.boundValueOps(redisKey).setIfAbsent(value);
	}

	public Object getByKey(RedisKey mainKey, Object subKey){
		String redisKey = getRedisKey(mainKey, subKey);
		return redisTemplate.boundValueOps(redisKey).get();
	}

	public void deleteByKey(RedisKey mainKey, Object subKey) {
		String redisKey = getRedisKey(mainKey, subKey);
		redisTemplate.boundValueOps(redisKey).getOperations().delete(redisKey);
	}

	public void setExpireSecondsAtValueOps(RedisKey mainKey, Object subKey, int seconds){
		String redisKey = getRedisKey(mainKey, subKey);
		redisTemplate.boundValueOps(redisKey).expire(seconds, TimeUnit.SECONDS);
	}

	public boolean existsByKey(RedisKey mainKey, Object subKey){
		String redisKey = getRedisKey(mainKey, subKey);
		return redisTemplate.boundValueOps(redisKey).getOperations().hasKey(redisKey);
	}

	private String getRedisKey(RedisKey mainKey, Object subKey){
		return mainKey.name() + ":" + subKey;
	}

}
