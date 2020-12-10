package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.QCardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.common.QConnectedMng;
import com.nomadconnection.dapp.core.domain.common.QIssuanceProgress;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.corp.QCorp;
import com.nomadconnection.dapp.core.domain.res.QResAccount;
import com.nomadconnection.dapp.core.domain.res.QResBatch;
import com.nomadconnection.dapp.core.domain.res.QResBatchList;
import com.nomadconnection.dapp.core.domain.res.ResBatchType;
import com.nomadconnection.dapp.core.domain.risk.QRisk;
import com.nomadconnection.dapp.core.domain.user.QUser;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CorpCustomRepositoryImpl extends QuerydslRepositorySupport implements CorpCustomRepository {

    private final QRisk risk = QRisk.risk;
    private final QUser user = QUser.user;
    private final QCorp corp = QCorp.corp;
    private final QResAccount resAccount = QResAccount.resAccount1;
    private final QConnectedMng connectedMng = QConnectedMng.connectedMng;
    private final QResBatch resBatch = QResBatch.resBatch;
    private final QResBatchList resBatchList = QResBatchList.resBatchList;
    private final QIssuanceProgress issuanceProgress = QIssuanceProgress.issuanceProgress;
    private final QCardIssuanceInfo cardIssuanceInfo = QCardIssuanceInfo.cardIssuanceInfo;


    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     */
    public CorpCustomRepositoryImpl() {
        super(Corp.class);
    }

    @Override
    public Page<SearchCorpResultDto> corpList(SearchCorpDto dto, Long idxUser, Pageable pageable) {

        List<SearchCorpResultDto> riskList;
        JPQLQuery<SearchCorpResultDto> query = from(corp)
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
//                        ExpressionUtils.as(
//                                JPAExpressions.select(resBatchList.count().eq((long) 0))
//                                        .from(resBatchList)
//                                        .where(resBatchList.errCode.notEqualsIgnoreCase("CF-00000"))
//                                        .where(resBatchList.resBatchType.eq(ResBatchType.BANK))
//                                        .where(resBatchList.idxResBatch.eq(
//                                                JPAExpressions.select(resBatch.idx.max())
//                                                        .from(resBatch)
//                                                        .where(resBatch.idxUser.eq(corp.user.idx))))
//                                , "boolError"),
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
            query.where(corp.resCompanyNm.contains(dto.getResCompanyNm()));
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

    @Override
    public Page<SearchCorpResultDto> adminCorpList(SearchCorpListDto dto, Long idxUser, Pageable pageable) {

        List<SearchCorpListDto> riskList;
        JPQLQuery<SearchCorpListDto> query = from(corp)
                .leftJoin(issuanceProgress).on(corp.idx.eq(issuanceProgress.corpIdx))
                .leftJoin(cardIssuanceInfo).on(corp.idx.eq(cardIssuanceInfo.corp.idx))
                .select(Projections.bean(SearchCorpListDto.class,
                        corp.idx.as("idx"),
                        corp.resCompanyNm.as("resCompanyNm"),
                        corp.resCompanyIdentityNo.as("resCompanyIdentityNo"),
                        corp.resUserNm.as("ceoName"),
                        corp.user.cardCompany.as("cardCompany"),
                    new CaseBuilder()
                        .when(cardIssuanceInfo.issuanceStatus.eq(IssuanceStatus.ISSUED))
                        .then(true)
                        .otherwise(false).as("cardIssuance"),
                        cardIssuanceInfo.issuanceStatus.as("issuanceStatus"),
                        cardIssuanceInfo.issuanceDepth.as("issuanceDepth"),
                        corp.user.name.as("userName"),
                        corp.user.email.as("email"),
                        corp.resRegisterDate.as("registerDate"),
                        issuanceProgress.createdAt.as("applyDate"),
                        issuanceProgress.updatedAt.as("decisionDate")
                ));

        query.where(corp.riskConfig.enabled.isTrue());

        if (dto.getResCompanyNm() != null) {
            query.where(corp.resCompanyNm.toLowerCase().contains(dto.getResCompanyNm().toLowerCase()));
        }

        if (dto.getResCompanyIdentityNo() != null) {
            query.where(corp.resCompanyIdentityNo.toLowerCase().contains(dto.getResCompanyIdentityNo().toLowerCase()));
        }

        if (dto.getCardCompany() != null) {
            query.where(corp.user.cardCompany.eq(dto.getCardCompany()));
        }

        if (dto.getUserName() != null) {
            query.where(corp.user.name.toLowerCase().contains(dto.getUserName().toLowerCase()));
        }

        if (dto.getEmail() != null) {
            query.where(corp.user.email.toLowerCase().contains(dto.getEmail().toLowerCase()));
        }

        riskList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(riskList, pageable, query.fetchCount());
    }

    @Override
    public Page<ScrapCorpListDto> scrapCorpList(ScrapCorpDto dto, Pageable pageable){

        LocalDate ld = LocalDate.parse(dto.getUpdatedAt().replaceAll("-", ""), DateTimeFormatter.BASIC_ISO_DATE);
        LocalDateTime from = ld.atTime(0,0);
        LocalDateTime to = from.plusDays(1);

        JPQLQuery<ScrapCorpListDto> query = from(corp)
                .leftJoin(risk).on(risk.date.eq(ld.format(DateTimeFormatter.BASIC_ISO_DATE))
                        // .and(risk.grade.in( dto.getGrade()!=null? dto.getGrade():"A","B","C" ))
                        .and(risk.corp.idx.eq(corp.idx)))
                .leftJoin(resBatch).on(corp.user.idx.eq(resBatch.idxUser).and(resBatch.createdAt.between(from,to)))
                .select(Projections.bean(ScrapCorpListDto.class,
                        corp.idx.as("idxCorp"),
                        corp.resCompanyNm.as("corpName"),
                        corp.user.cardCompany.as("cardCompany"),
                        risk.grade.as("grade"),
                        ExpressionUtils.as(
                                JPAExpressions.select(resAccount.count())
                                        .from(resAccount)
                                        .where(resAccount.connectedId.in(
                                                JPAExpressions.select(connectedMng.connectedId)
                                                        .from(connectedMng)
                                                        .where(connectedMng.idxUser.eq(corp.user.idx))
                                        ).and(resAccount.enabled.eq(true)))
                                ,"total"),
                        ExpressionUtils.as(
                                JPAExpressions.select(resBatchList.count())
                                        .from(resBatchList)
                                        .where(resBatchList.idxResBatch.eq(resBatch.idx)
                                        .and(resBatchList.resBatchType.eq(ResBatchType.ACCOUNT))
                                        )
                                ,"progress"),
                        resBatch.endFlag.as("endFlag"),
                        resBatch.updatedAt.as("updatedAt"),
                        resBatch.createdAt.as("createdAt")
                ));

        query.where(resBatch.createdAt.between(from,to));

        if (dto.getCorpName() != null) {
            query.where(corp.resCompanyNm.toLowerCase().contains(dto.getCorpName().toLowerCase()));
        }

        if (dto.getTypeAB() != null) {
            query.where(risk.cardType.eq(dto.getTypeAB()));
        }

        if (dto.getCardCompany() != null) {
            query.where(corp.user.cardCompany.eq(dto.getCardCompany()));
        }

        if (dto.getGrade() != null) {
            query.where(risk.grade.toLowerCase().eq(dto.getGrade().toLowerCase()));
        }

        if( pageable.getSort().isEmpty()) {
            query.orderBy(resBatch.idx.desc());
        }

        List<ScrapCorpListDto> list = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(list, pageable, query.fetchCount());
    }
}
