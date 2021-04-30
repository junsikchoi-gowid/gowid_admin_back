package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.flow.FlowReportMonth;
import com.nomadconnection.dapp.core.domain.flow.QFlowReportMonth;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class FlowReportMonthCustomRepositoryImpl extends QuerydslRepositorySupport implements FlowReportMonthCustomRepository {

    private  final QFlowReportMonth flowReportMonth = QFlowReportMonth.flowReportMonth;

    public FlowReportMonthCustomRepositoryImpl() {
        super(FlowReportMonth.class);
    }
}
