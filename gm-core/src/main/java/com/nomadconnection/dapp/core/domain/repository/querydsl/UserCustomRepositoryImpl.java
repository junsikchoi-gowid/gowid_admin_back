package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.*;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Collections;
import java.util.stream.Stream;

public class UserCustomRepositoryImpl extends QuerydslRepositorySupport implements UserCustomRepository {

	private final QUser user = QUser.user;
	private final QCard card = QCard.card;

	/**
	 * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
	 */
	public UserCustomRepositoryImpl() {
		super(User.class);
	}

	/**
	 * 법인에 소속된 모든 멤버 조회
	 *
	 * @param corp 엔터티(법인)
	 * @return 멤버목록
	 */
	public Stream<User> findCorpMembers(Corp corp, String keyword) {
		if (corp == null) {
			return Stream.empty();
		}
		final JPQLQuery<User> query = from(user).leftJoin(user.dept).where(user.corp.eq(corp));
		if (keyword != null) {
			query.where(user.name.contains(keyword)
					.or(user.email.contains(keyword))
					.or(user.dept.name.contains(keyword))
			);
		}
		return query.fetch().stream();
	}

	public Stream<User> findDeptMembers(Corp corp, Long idxDept) {
		if (corp == null) {
			return Stream.empty();
		}
		final JPQLQuery<User> query = from(user).where(user.corp.eq(corp));
		{
			if (idxDept == null) {
				query.where(user.dept.isNull());
			} else {
				query.where(user.dept.idx.eq(idxDept));
			}
		}
		return query.fetch().stream();
	}
}
