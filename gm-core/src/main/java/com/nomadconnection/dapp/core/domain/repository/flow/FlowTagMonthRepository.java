package com.nomadconnection.dapp.core.domain.repository.flow;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.flow.FlowTagMonth;
import com.nomadconnection.dapp.core.domain.flow.FlowTagConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FlowTagMonthRepository extends JpaRepository<FlowTagMonth, Long> {

    List<FlowTagMonth> findByCorpAndFlowDateBetween(Corp corp, String from, String to);

    FlowTagMonth findTopByCorpOrderByUpdatedAtDesc(Corp corp);

    Optional<FlowTagMonth> findTopByCorpAndFlowDate(Corp corp, String month);

    Optional<FlowTagMonth> findByCorpAndFlowDate(Corp corp, String month);


}
