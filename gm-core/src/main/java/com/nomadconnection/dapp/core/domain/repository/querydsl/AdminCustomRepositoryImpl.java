package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.common.QCommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.QConnectedMng;
import com.nomadconnection.dapp.core.domain.corp.QCorp;
import com.nomadconnection.dapp.core.domain.res.QResAccount;
import com.nomadconnection.dapp.core.domain.res.QResAccountHistory;
import com.nomadconnection.dapp.core.domain.res.QResBatch;
import com.nomadconnection.dapp.core.domain.res.QResBatchList;
import com.nomadconnection.dapp.core.domain.risk.QRisk;
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
    private final QUser user = QUser.user;
    private final QCorp corp = QCorp.corp;
    private final QCommonCodeDetail commonCodeDetail = QCommonCodeDetail.commonCodeDetail;
    private final QResAccount resAccount1 = QResAccount.resAccount1;
    private final QResAccountHistory resAccountHistory = QResAccountHistory.resAccountHistory;
    private final QConnectedMng connectedMng = QConnectedMng.connectedMng;
    private final QResBatch resBatch = QResBatch.resBatch;
    private final QResBatchList resBatchList = QResBatchList.resBatchList;

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     */
    public AdminCustomRepositoryImpl() {
        super(Risk.class);
    }

    @Override
    public Page<SearchRiskResultDto> riskList(SearchRiskDto dto, Long idxUser, Pageable pageable) {

        List<SearchRiskResultDto> riskList;

        JPQLQuery<SearchRiskResultDto> query = from(risk)
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
                                        .where(resAccount1.resAccountDeposit.in("10","11","12","13","14"))
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
                        risk.cardIssuance.as("cardIssuance"),
                        risk.cardAvailable.as("cardAvailable"),
                        risk.pause.as("pause"),
                        risk.updatedAt.as("updatedAt"),
                        risk.errCode.as("errCode")
                ))
                ;

        query.where(risk.date.eq(LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE)));

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
            query.where(risk.cardIssuance.eq(dto.getCardIssuance().equals("true")));
        }

		if ( dto.getUpdatedStatus() != null ) {
			if (dto.getUpdatedStatus().toLowerCase().equals("true")){
                query.where(risk.errCode.isNull());
			}else{
                query.where(risk.errCode.isNotNull());
			}
		}

        riskList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(riskList, pageable, query.fetchCount());
    }
}
