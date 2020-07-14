package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.corp.Dept;
import com.nomadconnection.dapp.core.domain.corp.QDept;
import com.nomadconnection.dapp.core.domain.user.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeptCustomRepositoryImpl extends QuerydslRepositorySupport implements DeptCustomRepository {

	private final QUser user = QUser.user;
	private final QDept dept = QDept.dept;

	/**
	 * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
	 */
	public DeptCustomRepositoryImpl() {
		super(Dept.class);
	}

	@Override
	public List<DeptWithMemberCountDto> findDeptWithMemberCount(Corp corp) {
		if (corp == null) {
			return Collections.emptyList();
		}
		JPQLQuery<DeptWithMemberCountDto> q1 = from(dept).leftJoin(user).on(dept.eq(user.dept))
				.select(Projections.bean(DeptWithMemberCountDto.class,
						dept.idx.as("idx"),
						dept.name.as("name"),
						user.countDistinct().as("members")))
				.where(dept.corp.eq(corp))
				.groupBy(dept);
		JPQLQuery<DeptWithMemberCountDto> q2 = from(user)
				.select(Projections.bean(DeptWithMemberCountDto.class,
						Expressions.asString("미지정").as("name"),
						user.count().as("members")))
				.where(user.corp.eq(corp).and(user.dept.isNull()));
		return Stream.of(q1.fetch(), q2.fetch()).flatMap(Collection::stream).collect(Collectors.toList());
	}
}
