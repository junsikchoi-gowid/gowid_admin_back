package com.nomadconnection.dapp.core.domain.repository.flow;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.flow.FlowTagMonth;
import com.nomadconnection.dapp.core.dto.flow.FlowReportExcelDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FlowTagMonthRepository extends JpaRepository<FlowTagMonth, Long> {

    @Query(" select new com.nomadconnection.dapp.core.dto.flow.FlowReportExcelDto" +
            "( ftc.codeLv1, ftc.codeLv2, ftc.codeLv3, ftc.codeLv4, ftm.flowDate, ftm.flowTotal ) " +
            " from Corp c " +
            " left join FlowTagMonth ftm on c.idx = ftm.idxCorp " +
            " left join FlowTagConfig ftc on ftm.idxFlowTag = ftc.idx " +
            " where c.idx = :idxCorp " +
            " and ftm.flowDate BETWEEN substr( :fromDate, 1, 6) and substr( :toDate, 1, 6) " +
            " order by ftm.flowDate asc, ftc.codeLv1 desc, ftc.codeLv2 desc, ftc.codeLv3 desc, ftc.codeLv4 desc ")
    List<FlowReportExcelDto> findByCorpAndFlowDateBetween(@Param("idxCorp") Long idxCorp
            , @Param("fromDate") String fromDate
            , @Param("toDate") String toDate);

    Optional<FlowTagMonth> findTopByCorpAndFlowDate(Corp corp, String month);

    Optional<FlowTagMonth> findByCorpAndFlowDate(Corp corp, String month);
}