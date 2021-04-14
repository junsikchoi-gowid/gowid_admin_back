package com.nomadconnection.dapp.core.domain.repository.querydsl;

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
    public Page<CorpListDto> adminCorpListV2(SearchCorpListDtoV2 dto, Pageable pageable) {
            List<CorpListDto> list;

            JPQLQuery<CorpListDto> query = from(corp)
                .leftJoin(user).on(corp.user.idx.eq(user.idx))
                .leftJoin(cardIssuanceInfo).on(user.idx.eq(cardIssuanceInfo.user.idx))
                .leftJoin(issuanceProgress).on(corp.idx.eq(issuanceProgress.corpIdx))
                .select(Projections.bean(CorpListDto.class,
                    user.idx.as("idxUser"),
                    corp.idx.as("idxCorp"),
                    cardIssuanceInfo.idx.as("idxCardIssuanceInfo"),
                    corp.resCompanyNm.as("resCompanyNm"),
                    corp.resCompanyIdentityNo.as("resCompanyIdentityNo"),
                    user.name.as("userName"),
                    user.cardCompany.as("cardCompany"),
                    cardIssuanceInfo.card.hopeLimit.as("hopeLimit"),
                    cardIssuanceInfo.card.grantLimit.as("grantLimit"),
                    cardIssuanceInfo.issuanceStatus.as("issuanceStatus"),
                    cardIssuanceInfo.issuanceDepth.as("issuanceDepth"),
                    corp.createdAt.as("corpRegisterDate"),
                    issuanceProgress.createdAt.as("applyDate"),
                    issuanceProgress.updatedAt.as("decisionDate")
                ));
            query.where(corp.user.authentication.enabled.isTrue());

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

            if (dto.getIssuanceStatus() != null) {
                if (dto.getIssuanceStatus().size() > 1) {
                    query.where(cardIssuanceInfo.issuanceStatus.eq(dto.getIssuanceStatus().get(0))
                        .or(cardIssuanceInfo.issuanceStatus.eq(dto.getIssuanceStatus().get(1))));
                } else {
                    query.where(cardIssuanceInfo.issuanceStatus.eq(dto.getIssuanceStatus().get(0)));
                }
            }

            if (dto.getIssuanceDepth() != null) {
                query.where(cardIssuanceInfo.issuanceDepth.eq(dto.getIssuanceDepth()));
            }

            list = getQuerydsl().applyPagination(pageable, query).fetch();

            return new PageImpl(list, pageable, query.fetchCount());
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
