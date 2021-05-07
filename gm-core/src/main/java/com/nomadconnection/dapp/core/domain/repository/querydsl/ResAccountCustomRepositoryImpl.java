package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.common.QConnectedMng;
import com.nomadconnection.dapp.core.domain.corp.QCorp;
import com.nomadconnection.dapp.core.domain.res.QResAccount;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.nomadconnection.dapp.core.domain.res.ResAccountStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.ObjectUtils;

import java.util.List;

public class ResAccountCustomRepositoryImpl extends QuerydslRepositorySupport implements ResAccountCustomRepository{

    private final QCorp corp = QCorp.corp;
    private final QResAccount resAccount = QResAccount.resAccount1;
    private final QConnectedMng connectedMng = QConnectedMng.connectedMng;

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     */
    public ResAccountCustomRepositoryImpl() {
        super(ResAccount.class);
    }

    @Override
    public List<ResAccountCustomRepository.CorpAccountDto> FlowAccountList(Long idxCorp, Boolean favorite){
        JPQLQuery<ResAccountCustomRepository.CorpAccountDto> query = from(corp)
                .leftJoin(connectedMng).on(connectedMng.corp.eq(corp))
                .leftJoin(resAccount).on(resAccount.connectedId.eq(connectedMng.connectedId).and(resAccount.enabled.isNull().or(resAccount.enabled.isTrue())))
                .select(Projections.bean(ResAccountCustomRepository.CorpAccountDto.class,
                        resAccount.idx.as("idxResAccount"),
                        resAccount.favorite.as("favorite"),
                        resAccount.nickName.as("nickName"),
                        resAccount.resAccountName.as("resAccountName"),
                        resAccount.resAccountNickName.as("resAccountNickName"),
                        resAccount.resAccountCurrency.as("currency"),
                        resAccount.type.as("type"),
                        resAccount.organization.as("organization"),
                        resAccount.resAccount.as("resAccount"),
                        resAccount.resAccountBalance.as("resAccountBalance"),
                        resAccount.resOverdraftAcctYN.as("resOverdraftAcctYN"),
                        resAccount.resAccountDisplay.as("resAccountDisplay"),
                        resAccount.status.as("status"),
                        resAccount.statusDesc.as("statusDesc")
                        ))
                .where(corp.idx.eq(idxCorp).and(resAccount.status.ne(ResAccountStatus.DELETE)))
                ;

        if(!ObjectUtils.isEmpty(favorite)){
            query.where(resAccount.favorite.eq(favorite));
        }

        return getQuerydsl().applySorting(Sort.unsorted(), query).fetch();
    }

}
