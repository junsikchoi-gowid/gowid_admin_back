package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.QCardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.corp.QCorp;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class CardIssunaceInfoCustomRepositoryImpl extends QuerydslRepositorySupport implements CardIssunaceInfoCustomRepository {
    private final QCorp corp = QCorp.corp;
    private final QCardIssuanceInfo cardIssuanceInfo = QCardIssuanceInfo.cardIssuanceInfo;

    public CardIssunaceInfoCustomRepositoryImpl() {
        super(CardIssuanceInfo.class);
    }

    @Override
    public CardIssuanceInfoDto issuanceInfo(Long idxCardIssuanceInfo) {
        JPQLQuery<CardIssuanceInfoDto> query = from(cardIssuanceInfo)
            .leftJoin(corp).on(cardIssuanceInfo.corp.idx.eq(corp.idx))
            .select(Projections.constructor(CardIssuanceInfoDto.class,
                cardIssuanceInfo.cardCompany.as("cardCompany"),
                cardIssuanceInfo.issuanceDepth.as("issuanceDepth"),
                cardIssuanceInfo.issuanceStatus.as("issuanceStatus"),
                cardIssuanceInfo.card.hopeLimit.as("hopeLimit"),
                cardIssuanceInfo.card.grantLimit.as("grantLimit"),
                cardIssuanceInfo.card.requestCount.as("requestCount"),
                cardIssuanceInfo.card.lotteGreenCount.as("lotteGreenCount"),
                cardIssuanceInfo.card.lotteBlackCount.as("lotteBlackCount"),
                cardIssuanceInfo.card.lotteGreenTrafficCount.as("lotteGreenTrafficCount"),
                cardIssuanceInfo.card.lotteBlackTrafficCount.as("lotteBlackTrafficCount"),
                cardIssuanceInfo.card.lotteHiPassCount.as("lotteHiPassCount"),
                cardIssuanceInfo.appliedAt.as("applyDate"),
                cardIssuanceInfo.issuedAt.as("decisionDate")
            ));
        query.where(cardIssuanceInfo.idx.eq(idxCardIssuanceInfo));

        return query.fetch().get(0);
    }
}
