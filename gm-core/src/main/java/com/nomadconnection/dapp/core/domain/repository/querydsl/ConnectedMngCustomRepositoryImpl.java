package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.common.ConnectedMng;
import com.nomadconnection.dapp.core.domain.common.QConnectedMng;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.res.*;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ConnectedMngCustomRepositoryImpl extends QuerydslRepositorySupport implements ConnectedMngCustomRepository {

    private final QConnectedMng connectedMng = QConnectedMng.connectedMng;
    private final QResAccount resAccount1 = QResAccount.resAccount1;
    private final QResAccountHistory resAccountHistory = QResAccountHistory.resAccountHistory;

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     */
    public ConnectedMngCustomRepositoryImpl() {
        super(ConnectedMng.class);
    }

    @Override
    public ResAccountHistory accountHistoryUpdateTime(Corp corp) {

        JPQLQuery<ResAccountHistory> query = from(connectedMng)
                .join(resAccount1).on(resAccount1.connectedId.eq(connectedMng.connectedId))
                .join(resAccountHistory).on(resAccountHistory.resAccount.eq(resAccount1.resAccount))
                .select(resAccountHistory)
                .where(connectedMng.corp.eq(corp))
                .orderBy(resAccountHistory.updatedAt.desc())
                .limit(1L);

        return query.fetchOne();
    }

    public ResAccount accountUpdateTime(Corp corp) {

        JPQLQuery<ResAccount> query = from(connectedMng)
                .join(resAccount1).on(resAccount1.connectedId.eq(connectedMng.connectedId))
                .select(resAccount1)
                .where(connectedMng.corp.eq(corp))
                .orderBy(resAccount1.updatedAt.desc())
                .limit(1L);

        return query.fetchOne();
    }

    @Override
    public List<ResAccount> accountList(Corp corp) {

        JPQLQuery<ResAccount> query = from(connectedMng)
                .join(resAccount1).on(resAccount1.connectedId.eq(connectedMng.connectedId)
                        .and(resAccount1.status.in(ResAccountStatus.ERROR, ResAccountStatus.NORMAL, ResAccountStatus.STOP)))
                .select(resAccount1)
                .where(connectedMng.corp.eq(corp));

        return query.fetch();
    }
}
