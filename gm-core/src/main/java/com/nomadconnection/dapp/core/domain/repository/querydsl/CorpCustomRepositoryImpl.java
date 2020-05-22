package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.*;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class CorpCustomRepositoryImpl extends QuerydslRepositorySupport implements CorpCustomRepository {

    private final QRisk risk = QRisk.risk;
    private final QUser user = QUser.user;
    private final QCorp corp = QCorp.corp;
    private final QResAccount resAccount1 = QResAccount.resAccount1;
    private final QConnectedMng connectedMng = QConnectedMng.connectedMng;
    private final QResBatch resBatch = QResBatch.resBatch;
    private final QResBatchList resBatchList = QResBatchList.resBatchList;

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     */
    public CorpCustomRepositoryImpl() {
        super(Corp.class);
    }

    @Override
    public Page<SearchCorpResultDto> corpList(SearchCorpDto dto, Long idxUser, Pageable pageable) {

        final List<SearchCorpResultDto> riskList;
        final JPQLQuery<SearchCorpResultDto> query = from(corp)
                .select(Projections.bean(SearchCorpResultDto.class,
                        corp.idx.as("idx"),
                        corp.resCompanyNm.as("resCompanyNm"),
                        corp.resCompanyIdentityNo.as("resCompanyIdentityNo"),
                        corp.resUserNm.as("resUserNm"),
                        corp.createdAt.as("createdAt"),
                        corp.resBusinessItems.as("resBusinessItems"),
                        corp.resBusinessTypes.as("resBusinessTypes"),
                        corp.riskConfig.ceoGuarantee.as("ceoGuarantee"),
                        corp.riskConfig.depositGuarantee.as("depositGuarantee"),
                        corp.riskConfig.depositPayment.as("depositPayment"),
                        corp.riskConfig.cardIssuance.as("cardIssuance"),
                        corp.riskConfig.ventureCertification.as("ventureCertification"),
                        corp.riskConfig.vcInvestment.as("vcInvestment"),
                        ExpressionUtils.as(
                                JPAExpressions.select(resBatchList.count().eq((long) 0))
                                        .from(resBatchList)
                                        .where(resBatchList.errCode.notEqualsIgnoreCase("CF-00000"))
                                        .where(resBatchList.resBatchType.eq(ResBatchType.BANK))
                                        .where(resBatchList.idxResBatch.eq(
                                                JPAExpressions.select(resBatch.idx.max())
                                                        .from(resBatch)
                                                        .where(resBatch.idxUser.eq(corp.user.idx))))
                                , "boolError"),
                        ExpressionUtils.as(
                                JPAExpressions.select((risk.emergencyStop.castToNum(Long.class).add(risk.emergencyStop.castToNum(Long.class))).eq((long)0))
                                        .from(risk)
                                        .where(risk.idx.eq(
                                                JPAExpressions.select(risk.idx.max())
                                                        .from(risk)
                                                        .where(risk.corp.idx.eq(corp.idx))))
                                , "boolPauseStop")
                ));

        query.where(corp.riskConfig.enabled.isTrue());

        if (dto.getResCompanyNm() != null) {
            query.where(corp.resCompanyNm.like(dto.getResCompanyNm()));
        }

        if (dto.getResCompanyIdentityNo() != null) {
            query.where(corp.resCompanyIdentityNo.eq(dto.getResCompanyIdentityNo()));
        }

        if (dto.getVentureCertification() != null) {
            query.where(corp.riskConfig.ventureCertification.eq(dto.getVentureCertification().toLowerCase().equals("true")));
        }

        if (dto.getVcInvestment() != null) {
            query.where(corp.riskConfig.vcInvestment.eq(dto.getVcInvestment().toLowerCase().equals("true")));
        }

        riskList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(riskList, pageable, query.fetchCount());
    }
}
