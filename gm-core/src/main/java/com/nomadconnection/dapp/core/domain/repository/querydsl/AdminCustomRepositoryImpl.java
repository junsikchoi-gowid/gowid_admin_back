package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.*;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

public class AdminCustomRepositoryImpl extends QuerydslRepositorySupport implements AdminCustomRepository{

	private final QRisk risk = QRisk.risk;
	private final QUser user = QUser.user;
	private final QCorp corp = QCorp.corp;
	private final QResAccount resAccount = QResAccount.resAccount1;
	private final QConnectedMng connectedMng = QConnectedMng.connectedMng;


	/**
	 * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
	 */
	public AdminCustomRepositoryImpl() {
		super(Risk.class);
	}

	@Override
	public Page<RiskCustomDto> riskList(SearchRiskDto dto, Long idx, Pageable pageable) {
		
		final JPQLQuery<RiskCustomDto> query = from(risk)
				.join(user).on(user.idx.eq(risk.idxUser))
				.join(corp).on(corp.idx.eq(user.corp.idx))
				.join(connectedMng).on(connectedMng.idxUser.eq(risk.idxUser))
				.join(resAccount).on(connectedMng.connectedId.eq(resAccount.connectedId))
				.select(Projections.bean(RiskCustomDto.class,
						corp.resCompanyNm.as("idxCorpName"),
						resAccount.resAccountBalance.castToNum(Float.class).sum().as("Balance"),
						risk.cardLimitNow.as("cardLimitNow"),
						risk.currentBalance.as("currentBalance"),
						risk.grade.as("grade")))
				.groupBy(corp.resCompanyNm,risk.cardLimitNow,risk.currentBalance,risk.grade )
				;


		if(dto.idxCorpName != null ){
			// query.where( risk.irType.eq(dto.irType()));
		}

		if ( dto.getGrade() != null ) {
			query.where(risk.grade.eq(dto.getGrade()));
		}

		if ( dto.getEmergencyStop() != null ) {
			query.where(risk.emergencyStop.eq(dto.getEmergencyStop().equals("true")));
		}

		if ( dto.getCardIssuance() != null ) {
			query.where(risk.cardIssuance.eq(dto.getCardIssuance().equals("true")));
		}

		if ( dto.getUpdateAt() != null ) {
			// query.(risk.updatedAt( ))
		}

		/*
		if(sortBy != null) {
			if (sortBy.toLowerCase().equals("asc")) {
				query.orderBy(QRisk.risk.createdAt.asc());
			} else if (sortBy.toLowerCase().equals("desc")) {
				query.orderBy(QRisk.risk.createdAt.desc());
			}
		}
		*/

		final List<RiskCustomDto> riskList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl(riskList, pageable, query.fetchCount());
	}
}
