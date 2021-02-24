package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.v2.dto.DashBoardDto;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashBoardService {

	private final CardIssuanceInfoRepository repoCardIssuanceInfo;
	private final CommonCodeDetailRepository repoCommonCodeDetail;


	@Transactional
	public List<DashBoardDto.Card> getDashBoardCard() {

		List<DashBoardDto.Card> dashBoards = new ArrayList<>();
		dashBoards.add(getDashBoardCardCompany(CardCompany.SHINHAN));
		dashBoards.add(getDashBoardCardCompany(CardCompany.LOTTE));

		Integer corpCnt = 0;
		Long maxLimit = 0L;
		Long grantLimit = 0L;

		for( DashBoardDto.Card dashData : dashBoards){
			corpCnt += dashData.getCorpCnt();
			grantLimit += dashData.getGrantLimit();
			maxLimit += dashData.getMaxLimit();
		}

		log.debug( "- {}, {} , {} , {} " , corpCnt, grantLimit , maxLimit, grantLimit / maxLimit * 100L );

		dashBoards.add(DashBoardDto.Card.builder()
				.cardCompany(null)
				.corpCnt(corpCnt)
				.limitPercent(grantLimit * 100.0 /maxLimit )
				.maxLimit(maxLimit)
				.grantLimit(grantLimit)
				.build()
		);

		return dashBoards;
	}

	private DashBoardDto.Card getDashBoardCardCompany(CardCompany company) {

		int corpCnt;
		double limitPercent = 0.0;
		Long grantLimit;
		String maxLimit;

		corpCnt = repoCardIssuanceInfo.countByCardCompanyAndIssuanceStatus(company, IssuanceStatus.ISSUED);

		maxLimit = repoCommonCodeDetail.findFirstByCode1AndCode(company.name(), CommonCodeType.TOTAL_LIMIT).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("CommonCodeDetail")
						.build()
		).value1();

		log.debug(company.name());
		grantLimit = repoCardIssuanceInfo.sumGrantLimit(company.name());

		if(grantLimit != null)
		limitPercent = grantLimit * 100.0 / Long.parseLong(maxLimit);

		return DashBoardDto.Card.builder()
				.cardCompany(company)
				.corpCnt(corpCnt)
				.limitPercent(limitPercent)
				.maxLimit(Long.parseLong(maxLimit))
				.grantLimit(grantLimit)
				.build();
	}

	@Transactional
	public List<DashBoardDto.Month> getDashBoardMonth() {

		return repoCardIssuanceInfo.findDashBoardMonth().stream().map(DashBoardDto.Month::from)
		.collect(Collectors.toList());
	}

	@Transactional
	public List<DashBoardDto.Week> getDashBoardWeek() {

		return repoCardIssuanceInfo.findDashBoardWeek().stream().map(DashBoardDto.Week::from)
				.collect(Collectors.toList());
	}
}
