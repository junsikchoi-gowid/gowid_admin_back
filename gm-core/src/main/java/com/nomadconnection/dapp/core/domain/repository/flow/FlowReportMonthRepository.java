package com.nomadconnection.dapp.core.domain.repository.flow;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.flow.FlowReportMonth;
import io.swagger.annotations.OAuth2Definition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlowReportMonthRepository extends JpaRepository<FlowReportMonth, Long> {

    List<FlowReportMonth> findByCorpAndFlowDateBetweenOrderByFlowDateAsc(Corp corp, String startDate, String endDate);

    FlowReportMonth findTopByCorpOrderByUpdatedAtDesc(Corp corp);

    Optional<FlowReportMonth> findByCorpAndFlowDate(Corp corp, String flowDate);
    @Transactional
    @Modifying
    @Query(value = "delete from FlowReportMonth  where idxCorp = :idxCorp and flowDate between :start and :end ", nativeQuery = true)
    void delFlowReportMonth(@Param("idxCorp") Long idxCorp, @Param("start") String start, @Param("end") String end);
}
