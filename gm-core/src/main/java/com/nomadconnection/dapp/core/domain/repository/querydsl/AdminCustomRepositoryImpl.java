package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.QCardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.common.QCommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.QConnectedMng;
import com.nomadconnection.dapp.core.domain.corp.QCorp;
import com.nomadconnection.dapp.core.domain.res.QResAccount;
import com.nomadconnection.dapp.core.domain.res.QResAccountHistory;
import com.nomadconnection.dapp.core.domain.res.QResBatch;
import com.nomadconnection.dapp.core.domain.res.QResBatchList;
import com.nomadconnection.dapp.core.domain.risk.QRisk;
import com.nomadconnection.dapp.core.domain.risk.QRiskTrans;
import com.nomadconnection.dapp.core.domain.risk.Risk;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminCustomRepositoryImpl extends QuerydslRepositorySupport implements AdminCustomRepository {

    private final QRisk risk = QRisk.risk;
    private final QRiskTrans riskTrans = QRiskTrans.riskTrans;
    private final QUser user = QUser.user;
    private final QCorp corp = QCorp.corp;
    private final QCommonCodeDetail commonCodeDetail = QCommonCodeDetail.commonCodeDetail;
    private final QResAccount resAccount1 = QResAccount.resAccount1;
    private final QResAccountHistory resAccountHistory = QResAccountHistory.resAccountHistory;
    private final QConnectedMng connectedMng = QConnectedMng.connectedMng;
    private final QResBatch resBatch = QResBatch.resBatch;
    private final QResBatchList resBatchList = QResBatchList.resBatchList;
    private final QCardIssuanceInfo cardIssuanceInfo = QCardIssuanceInfo.cardIssuanceInfo;

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     */
    public AdminCustomRepositoryImpl() {
        super(Risk.class);
    }

    @Override
    public Page<SearchRiskResultDto> riskList(SearchRiskDto dto, Long idxUser, Pageable pageable) {

        String[] availableDepositCode = {"10","11","12","13","14"};
        List<SearchRiskResultDto> riskList;
        JPQLQuery<SearchRiskResultDto> query = from(risk)
                .join(cardIssuanceInfo).on(cardIssuanceInfo.corp.eq(risk.corp))
                .select(Projections.bean(SearchRiskResultDto.class,
                        risk.user.corp.idx.as("idxCorp"),
                        risk.user.corp.resCompanyNm.as("idxCorpName"),
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
                        risk.errCode.as("errCode")
                ))
                ;

        query.where(risk.date.eq(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)));

        if (dto.idxCorp != null) {
            query.where(corp.idx.eq(Long.parseLong(dto.getIdxCorp())));
        }

        if (dto.idxCorpName != null) {
            query.where(corp.resCompanyNm.contains(dto.getIdxCorpName()));
        }

        if (dto.getGrade() != null) {
            query.where(risk.grade.toLowerCase().eq(dto.getGrade().toLowerCase()));
        }

        if (dto.getEmergencyStop() != null) {
            query.where(risk.emergencyStop.eq(dto.getEmergencyStop().equals("true")));
        }

        if (dto.getPause() != null) {
            query.where(risk.pause.eq(dto.getPause().equals("true")));
        }

        if (dto.getCardIssuance() != null) {
            query.where(cardIssuanceInfo.issuanceStatus.eq(dto.getCardIssuance()));
        }

		if ( dto.getUpdatedStatus() != null ) {
			if (dto.getUpdatedStatus().toLowerCase().equals("true")){
                query.where(risk.errCode.isNull());
			}else{
                query.where(risk.errCode.isNotNull());
			}
		}

        if( pageable.getSort().isEmpty()) {
            query.orderBy(risk.idx.desc());
        }

        riskList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(riskList, pageable, query.fetchCount());
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

    @Override
    public Page<RiskTransDto> riskTransList(RiskOriginal dto, Long idxUser, Pageable pageable) {

        List<RiskTransDto> riskList;

        JPQLQuery<RiskTransDto> query = from(riskTrans)
                .select(Projections.bean(RiskTransDto.class,
                        riskTrans.idxCorp.as("idxCorp"),
                        riskTrans.resCompanyNm.as("idxCorpName"),
                        riskTrans.cardCompany.as("cardCompany"),
                        riskTrans.cardLimitNow.as("cardLimitNow"),
                        riskTrans.cardLimit.as("cardLimit"),
                        riskTrans.cashBalance.as("cashBalance"),
                        riskTrans.grade.as("grade"),
                        riskTrans.actualBalance.as("actualBalance"),
                        riskTrans.confirmedLimit.as("confirmedLimit"),
                        riskTrans.currentBalance.as("currentBalance"),
                        riskTrans.cardRestartCount.as("cardRestartCount"),
                        riskTrans.emergencyStop.as("emergencyStop"),
                        riskTrans.cardIssuance.as("cardIssuance"),
                        riskTrans.cardAvailable.as("cardAvailable"),
                        riskTrans.pause.as("pause"),
                        riskTrans.updatedAt.as("updatedAt"),
                        riskTrans.errCode.as("errCode")
                ));

        if (dto.getBaseDate() != null) {
            query.where(riskTrans.date.eq(dto.getBaseDate().replaceAll("-","")));
        }else{
            query.where(riskTrans.date.eq(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)));
        }

        if (dto.getCardType() != null ){
            query.where(riskTrans.cardType.eq(dto.getCardType()));
        }

        if (dto.getCardCompany() != null ){
            query.where(riskTrans.cardCompany.eq(dto.getCardCompany().getName()));
        }

        if (dto.getGrade() != null) {
            query.where(riskTrans.grade.toLowerCase().eq(dto.getGrade().toLowerCase()));
        }

        if (dto.getCardIssuance() != null) {
            query.where(riskTrans.cardIssuance.eq(dto.getCardIssuance().equals("true")));
        }

        if( pageable.getSort().isEmpty()) {
            query.orderBy(riskTrans.idx.desc());
        }

        riskList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(riskList, pageable, query.fetchCount());
    }

}
