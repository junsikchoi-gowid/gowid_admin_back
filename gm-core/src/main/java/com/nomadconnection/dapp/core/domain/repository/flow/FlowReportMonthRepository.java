package com.nomadconnection.dapp.core.domain.repository.flow;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.flow.FlowReportMonth;
import io.swagger.annotations.OAuth2Definition;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlowReportMonthRepository extends JpaRepository<FlowReportMonth, Long> {

    List<FlowReportMonth> findByCorpAndFlowDateBetween(Corp corp, String startDate, String endDate);

    FlowReportMonth findTopByCorpOrderByUpdatedAtDesc(Corp corp);

    Optional<FlowReportMonth> findByCorpAndFlowDate(Corp corp, String flowDate);
}
