package com.nomadconnection.dapp.redis.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.redis.enums.RedisKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedisServiceTest extends AbstractSpringBootTest {

	@Autowired
	private RedisService redisService;

	private Long id;
	private RedisKey key;

	@BeforeEach
	public void init(){
		id = 21L;
		key = RedisKey.CARD_ISSUANCE_INFO_IDX;
	}

	@Test
	@DisplayName("Redis에_저장하고_있는지_확인한다")
	public void exists(){
		redisService.putValue(key, id, true);
		redisService.setExpireSecondsAtValueOps(key, id, 500);

		assertEquals(true, redisService.getByKey(key, id));
		assertEquals(true, redisService.existsByKey(key, id));
	}

	@Test
	@DisplayName("Redis에_저장하고_삭제되는지_확인한다")
	public void notExists(){
		redisService.putValue(key, id, true);
		redisService.deleteByKey(key, id);

		assertEquals(false, redisService.existsByKey(key, id));
	}

}