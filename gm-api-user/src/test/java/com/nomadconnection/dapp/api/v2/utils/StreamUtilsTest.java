package com.nomadconnection.dapp.api.v2.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StreamUtilsTest {

	@Test
	void existsInList() {
		String target = "abc";
		List<String> list = Arrays.asList("abc","def", "gkk", "zzz");

		boolean existsIn = StreamUtils.in(list, target);

		assertThat(existsIn).isTrue();
	}

	@Test
	void notExistsIn(){
		String emptyString = null;
		List<String> emptyCollection = null;
		String target = "abc";
		List<String> list = Arrays.asList("yyy","def", "gkk", "zzz");

		boolean notExistsIn = StreamUtils.in(list, target);
		boolean notExistsInWhenEmptyTarget = StreamUtils.in(emptyCollection, target);
		boolean notExistsInWhenEmptyCollection = StreamUtils.in(list, emptyString);

		assertThat(notExistsIn).isFalse();
		assertThat(notExistsInWhenEmptyTarget).isFalse();
		assertThat(notExistsInWhenEmptyCollection).isFalse();
	}

}