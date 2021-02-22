package com.nomadconnection.dapp.core.domain.repository.limit;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.limit.ReviewStatus;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto;
import com.nomadconnection.dapp.core.dto.limit.LimitRecalculationDto.LimitRecalculationCondition;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.nomadconnection.dapp.core.domain.cardIssuanceInfo.QCardIssuanceInfo.cardIssuanceInfo;
import static com.nomadconnection.dapp.core.domain.corp.QCorp.corp;
import static com.nomadconnection.dapp.core.domain.limit.QLimitRecalculation.limitRecalculation;
import static com.nomadconnection.dapp.core.domain.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class LimitRecalculationQueryRepository {

	private final JPAQueryFactory queryFactory;

	public List<LimitRecalculationDto> findAll(LimitRecalculationCondition dto, int pageNo, int limit){

		return queryFactory
			.select(
				Projections.fields(LimitRecalculationDto.class,
					limitRecalculation.corp.idx.as("idxCorp"),
					corp.resCompanyNm.as("companyName"),
					corp.resCompanyIdentityNo.as("licenseNo"),
					cardIssuanceInfo.cardCompany.as("cardCompany"),
					limitRecalculation.hopeLimit.as("hopeLimit"),
					cardIssuanceInfo.card.calculatedLimit.castToNum(Long.class).as("calculatedLimit"),
					cardIssuanceInfo.card.grantLimit.castToNum(Long.class).as("grantLimit")
				)
			)
			.from(limitRecalculation)
			.innerJoin(corp).on(corp.eq(limitRecalculation.corp))
			.innerJoin(user).on(user.idx.eq(limitRecalculation.corp.user.idx))
			.innerJoin(cardIssuanceInfo).on(cardIssuanceInfo.corp.eq(limitRecalculation.corp))
			.where(
				likeCorpName(dto.getCompanyName()),
				likeLicenseNo(dto.getLicenseNo()),
				likeManager(dto.getManager()),
				likeEmail(dto.getEmail()),
				eqReviewStatus(dto.getReviewStatus()),
				eqIssuanceStatus(dto.getIssuanceStatus())
			)
			.offset(pageNo)
			.limit(limit)
			.orderBy(limitRecalculation.date.desc())
			.fetch();
	}

	private BooleanExpression likeCorpName(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return limitRecalculation.corp.resCompanyNm.contains(name);
	}

	private BooleanExpression likeLicenseNo(String licenseNo) {
		if (StringUtils.isEmpty(licenseNo)) {
			return null;
		}
		return limitRecalculation.corp.resCompanyIdentityNo.contains(licenseNo);
	}

	private BooleanExpression likeManager(String manager) {
		if (StringUtils.isEmpty(manager)) {
			return null;
		}
		return limitRecalculation.corp.user.name.contains(manager);
	}

	private BooleanExpression likeEmail(String email) {
		if (StringUtils.isEmpty(email)) {
			return null;
		}
		return limitRecalculation.corp.user.email.contains(email);
	}

	private BooleanExpression eqReviewStatus(ReviewStatus reviewStatus) {
		if (StringUtils.isEmpty(reviewStatus)) {
			return null;
		}
		return limitRecalculation.reviewStatus.eq(reviewStatus);
	}

	private BooleanExpression eqIssuanceStatus(IssuanceStatus issuanceStatus) {
		if (StringUtils.isEmpty(issuanceStatus)) {
			return null;
		}
		return limitRecalculation.corp.cardIssuanceInfo.issuanceStatus.eq(issuanceStatus);
	}

}
