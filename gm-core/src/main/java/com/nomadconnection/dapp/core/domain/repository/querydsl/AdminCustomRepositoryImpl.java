package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.QCardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.common.QConnectedMng;
import com.nomadconnection.dapp.core.domain.res.QResAccount;
import com.nomadconnection.dapp.core.domain.risk.QRisk;
import com.nomadconnection.dapp.core.domain.risk.Risk;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminCustomRepositoryImpl extends QuerydslRepositorySupport implements AdminCustomRepository {

    private final QRisk risk = QRisk.risk;
    private final QResAccount resAccount1 = QResAccount.resAccount1;
    private final QConnectedMng connectedMng = QConnectedMng.connectedMng;
    private final QCardIssuanceInfo cardIssuanceInfo = QCardIssuanceInfo.cardIssuanceInfo;

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     */
    public AdminCustomRepositoryImpl() {
        super(Risk.class);
    }

    @Override
    public Page<SearchRiskResultV2Dto> riskList(RiskOriginal dto, Long idxUser, Pageable pageable) {
        String[] availableDepositCode = {"10","11","12","13","14"};
        List<SearchRiskResultV2Dto> riskList;

        JPQLQuery<SearchRiskResultV2Dto> query = from(risk)
                .join(cardIssuanceInfo).on(cardIssuanceInfo.corp.eq(risk.corp))
                .select(Projections.bean(SearchRiskResultV2Dto.class,
                        risk.user.corp.idx.as("idxCorp"),
                        risk.user.corp.resCompanyNm.as("idxCorpName"),
                        // risk.user.cardCompany.as("cardCompany"),
                        cardIssuanceInfo.cardCompany.as("cardCompany"),
                        risk.cardLimitNow.as("cardLimitNow"),
                        risk.cardLimit.as("cardLimit"),
                        risk.cashBalance.as("cashBalance"),
                        risk.grade.as("grade"),
                        ExpressionUtils.as(
                                JPAExpressions.select(resAccount1.resAccountRiskBalance.sum())
                                        .from(resAccount1)
                                        .where(resAccount1.resAccountDeposit.in(availableDepositCode))
                                        .where(resAccount1.connectedId.in(
                                                JPAExpressions.select(connectedMng.connectedId)
                                                        .from(connectedMng)
                                                        .where(connectedMng.idxUser.eq(risk.user.idx))
                                        ))
                                ,"balance"),
                        risk.confirmedLimit.as("confirmedLimit"),
                        risk.currentBalance.as("currentBalance"),
                        risk.cardRestartCount.as("cardRestartCount"),
                        risk.emergencyStop.as("emergencyStop"),
                        cardIssuanceInfo.issuanceStatus.as("cardIssuance"),
                        risk.cardAvailable.as("cardAvailable"),
                        risk.pause.as("pause"),
                        risk.updatedAt.as("updatedAt"),
                        risk.realtimeLimit.as("realtimeLimit"),
                        risk.errCode.as("errCode"),
                        risk.cardType.as("cardType"),
                        risk.dma45.as("dma45"),
                        risk.dmm45.as("dmm45"),
                        risk.corp.riskConfig.hopeLimit.as("hopeLimit"),
                        risk.date.as("baseDate")
                        // risk.transFlag.as("transFlag")
                ))
                ;

        if (dto.idxCorp != null) {
            query.where(risk.corp.idx.eq(Long.parseLong(dto.getIdxCorp())));
        }else{
            if (dto.getBaseDate() != null) {
                query.where(risk.date.eq(dto.getBaseDate().replaceAll("-","")));
            }else{
                query.where(risk.date.eq(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)));
            }
        }

        if (dto.getResCompanyNm() != null) {
            query.where(risk.corp.resCompanyNm.contains(dto.getResCompanyNm()));
        }

        if (dto.getCardType() != null ){
            query.where(risk.cardType.eq(dto.getCardType()));
        }

        if (dto.getCardCompany() != null ){
            query.where(cardIssuanceInfo.cardCompany.eq(dto.getCardCompany()));
        }

        if (dto.getGrade() != null) {
            query.where(risk.grade.toLowerCase().eq(dto.getGrade().toLowerCase()));
        }

        if (dto.getCardIssuance() != null) {
            query.where(cardIssuanceInfo.issuanceStatus.eq(dto.getCardIssuance()));
        }

        if( pageable.getSort().isEmpty()) {
            query.orderBy(risk.idx.desc());
        }

        riskList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(riskList, pageable, query.fetchCount());
    }
}
