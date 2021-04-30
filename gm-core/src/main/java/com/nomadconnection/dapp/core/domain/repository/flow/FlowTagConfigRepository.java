package com.nomadconnection.dapp.core.domain.repository.flow;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.flow.FlowTagConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FlowTagConfigRepository extends JpaRepository<FlowTagConfig, Long> {

    @Override
    Optional<FlowTagConfig> findById(Long idx);

    List<FlowTagConfig> findByCorp(Corp corp);

    Optional<FlowTagConfig> findByCorpAndFlowCode(Corp corp, String flowCode);

    Optional<FlowTagConfig> findByCorpAndFlowCodeAndCode4(Corp corp, String flowCode, String code4);

    Optional<FlowTagConfig> findByCorpAndCodeLv3AndCodeLv4(Corp corp, String codeLv3, String codeLv4);

    Optional<FlowTagConfig> findByCorpAndCodeLv3AndCodeLv4AndIdxNot(Corp corp, String codeLv3, String codeLv4, Long idx);

    List<FlowTagConfig> findByCorpAndDeleteYnIsFalse(Corp corp);
}
