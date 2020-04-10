package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.Risk;
import com.nomadconnection.dapp.core.domain.QRisk;
import com.nomadconnection.dapp.core.domain.QResAccount;
import com.nomadconnection.dapp.core.domain.QCorp;
import com.nomadconnection.dapp.core.domain.QUser;
import com.nomadconnection.dapp.core.domain.QResBatchList;
import com.nomadconnection.dapp.core.domain.QConnectedMng;
import com.nomadconnection.dapp.core.domain.QResBatch;
import com.querydsl.core.types.Projections;
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
    private final QResAccount resAccount1 = QResAccount.resAccount1;
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
                        risk.user.corp.resCompanyNm.as("idxCorpName"),
                        risk.cardLimitNow.as("cardLimitNow"),
                        risk.cardLimit.as("cardLimit"),
                        risk.grade.as("grade"),
                        risk.recentBalance.as("balance"),
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
            query.where(corp.resCompanyNm.like(dto.getIdxCorpName()));
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



    /*public Page<RiskCustomDto> riskList2(SearchRiskDto dto, Long idxUser, Pageable pageable) {

        String strQuery =
                " select c.resCompanyNm as idxCorpName \n " +
                        "  , r.cardLimitNow as cardLimitNow  \n " +
                        "  , r.cardLimit as cardLimit  \n " +
                        "  , r.grade   " +
                        "  , (select sum(cast(resAccount1.resAccountBalance as decimal(19, 2)))  \n " +
                        "  from ResAccount resAccount1  \n " +
                        "  inner join ConnectedMng connectedMng on connectedMng.connectedId = resAccount1.connectedId  \n " +
                        "  where connectedMng.idxUser = r.idxUser) as balance,   \n " +
                        " r.currentBalance as currentBalance, r.cardRestartCount as cardRestartCount,   \n " +
                        " r.emergencyStop as emergencyStop, r.cardIssuance as cardIssuance, r.updatedAt as updatedAt,   \n " +
                        "  (select max(resBatchList.errCode)  \n " +
                        "  from ResBatchList resBatchList  \n " +
                        "  where resBatchList.idxResBatch = (select max(resBatch.idx)  \n " +
                        "  from ResBatch resBatch  \n " +
                        "  where resBatch.idxUser = r.idxUser) and resBatchList.resBatchType = 1 and resBatchList.errCode <> 'CF-00000') as updatedStatus  \n " +
                        " from Risk r  \n " +
                        "  inner join User u on u.idx = r.idxUser  \n " +
                        "  inner join Corp c on c.idx = u.idxCorp  \n " +
                        " where r.date = date_format(date_add(now(), INTERVAL - 1 day), '%Y%m%d' ) ";

        if (dto.idxCorpName != null) {
			strQuery.concat( " and c.resCompanyNm like CONCAT('%',:idxCorpName,'%') " );
        }

        if (dto.getGrade() != null) {
			strQuery.concat( " and risk.grade = :grade " );
        }

        if (dto.getEmergencyStop() != null) {
			strQuery.concat( " and risk.emergencyStop = :emergencyStop " );
        }

        if (dto.getCardIssuance() != null) {
			strQuery.concat( " and risk.cardIssuance = :cardIssuance " );
        }

		if (dto.getUpdatedStatus() != null) {
			if( dto.getUpdatedStatus().toLowerCase().equals("true")){
				strQuery.concat( " having updatedStatus is null " );
			}else{
				strQuery.concat( " having updatedStatus not is null " );
			}
		}

		if(pageable.getSort().isSorted()){
			strQuery.concat( " order by c.resCompanyNm asc" );
		}

        TypedQuery<RiskCustomDto> query = getEntityManager().createQuery(strQuery,RiskCustomDto.class);

		query.setParameter("idxCorpName",dto.getIdxCorpName());
		query.setParameter("grade",dto.getGrade());
		query.setParameter("emergencyStop",dto.getEmergencyStop());
		query.setParameter("cardIssuance",dto.getCardIssuance());

		query.setFirstResult(10);
		query.setMaxResults(10);

		final List<RiskCustomDto> riskList = query.getResultList();

        return new PageImpl(riskList, pageable, query.getMaxResults());
    }*/
}
