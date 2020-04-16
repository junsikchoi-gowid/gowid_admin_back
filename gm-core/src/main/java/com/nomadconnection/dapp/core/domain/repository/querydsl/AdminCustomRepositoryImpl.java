package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.*;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class AdminCustomRepositoryImpl extends QuerydslRepositorySupport implements AdminCustomRepository {

    private final QRisk risk = QRisk.risk;
    private final QUser user = QUser.user;
    private final QCorp corp = QCorp.corp;
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

        final List<SearchRiskResultDto> riskList;

        final JPQLQuery<SearchRiskResultDto> query = from(risk)
                .select(Projections.bean(SearchRiskResultDto.class,
                        risk.user.corp.idx.as("idxCorp"),
                        risk.user.corp.resCompanyNm.as("idxCorpName"),
                        risk.cardLimitNow.as("cardLimitNow"),
                        risk.cardLimit.as("cardLimit"),
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
                        risk.currentBalance.as("currentBalance"),
                        risk.cardRestartCount.as("cardRestartCount"),
                        risk.emergencyStop.as("emergencyStop"),
                        risk.cardIssuance.as("cardIssuance"),
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


    @Override
    public Page<CashResultDto> cashList( String searchCorpName, String updateStatus, Long idxUser, Pageable pageable) {

        final TypedQuery<CashResultDto> query =
                getEntityManager().createQuery("select idx, idxCorp, resCompanyNm, sum(resAccountIn) as resAccountIn, \n " +
                                "sum(resAccountOut) as resAccountOut,   \n" +
                                "max(befoBalance) as befoBalance, \n" +
                                "max(createdAt) as createdAt, \n" +
                                "max(errCode) as errCode FROM \n" +
                                " (select u.idx,  u.idxCorp,  \n c.resCompanyNm,  \n cm.connectedId,    \n" +
                                "ifnull((select sum(resAccountIn) from ResAccountHistory  \n" +
                                "where resAccountTrDate  =  Date_Format(now(),  '%Y%m%d') and resAccount in  \n" +
                                "(select resAccount from ResAccount d WHERE d.connectedId = cm.connectedId and  resAccountDeposit in ('10','11','12','13','14'))),0) \n" +
                                "as resAccountIn, ifnull((select sum(resAccountOut) from ResAccountHistory  where resAccountTrDate  =  Date_Format(now(),  '%Y%m%d') \n" +
                                "and resAccount in  (select resAccount from ResAccount d WHERE d.connectedId = cm.connectedId \n" +
                                "and  resAccountDeposit in ('10','11','12','13','14'))),0) as resAccountOut, ifnull((select currentBalance from Risk r\n" +
                                " where r.idxUser = u.idx and r.date = Date_Format(date_add(now(), INTERVAL - 1 DAY),  '%Y%m%d') ) , 0) befoBalance,        \n" +
                                " (select max(createdAt) from ResBatch where idxUser = u.idx) as createdAt,        \n" +
                                " (select max(errCode) from ResBatchList where errCode != 'CF-00000' and idxUser = u.idx and resBatchType = 1         \n" +
                                " and idxResBatch = (select max(idx) from ResBatch where idxUser = u.idx )) as errCode \n" +
                                " from User u  join Corp c on c.idx = u.idxCorp \n" +
                                " join ConnectedMng cm  on cm.idxUser = u.idx ) z "
//                        " where z.resCompanyNm like :resCompanyNm " +
//                        " and (z.errCode is null = :errCode)  " +
//                        "    group by idx  order by idx asc "
                        , CashResultDto.class );

        String ordering = "idx asc";
        if( searchCorpName.isEmpty()) searchCorpName = "";

        if(updateStatus.isEmpty()) updateStatus = null;

//        query.setParameter("resCompanyNm", "%"+ searchCorpName +"%");
//        query.setParameter( "errCode", updateStatus);
//        query.setParameter("orders" , ordering);

        query.setFirstResult((int)pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        int total = query.getMaxResults();
        List<CashResultDto> content = total > pageable.getOffset() ? query.getResultList() : Collections.<CashResultDto> emptyList();

        return new PageImpl(content, pageable, total);
    }

    @Override
    public Page<ErrorResultDto> errorList(ErrorResultDto risk, Long idxUser, Pageable pageable){

        return null;
    }
}
