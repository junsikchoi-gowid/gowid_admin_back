package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.common.QConnectedMng;
import com.nomadconnection.dapp.core.domain.corp.QCorp;
import com.nomadconnection.dapp.core.domain.res.QResAccount;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class ResAccountCustomRepositoryImpl extends QuerydslRepositorySupport implements ResAccountCustomRepository{

    private final QCorp corp = QCorp.corp;
    private final QResAccount resAccount = QResAccount.resAccount1;
    private final QConnectedMng connectedMng = QConnectedMng.connectedMng;
    private StringPath resAccountDepositOrder;

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     */
    public ResAccountCustomRepositoryImpl() {
        super(ResAccount.class);
    }

}
