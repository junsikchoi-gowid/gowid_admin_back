package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
