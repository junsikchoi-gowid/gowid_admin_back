package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.user.User;

import java.util.stream.Stream;

public interface UserCustomRepository {

	Stream<User> findCorpMembers(Corp corp, String keyword);
	Stream<User> findDeptMembers(Corp corp, Long idxDept);
}
