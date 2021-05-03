package com.nomadconnection.dapp.core.domain.repository.flow;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.flow.FlowComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlowCommentRepository extends JpaRepository<FlowComment, Long> {


    List<FlowComment> findByCorpAndEnabledOrderByCreatedAtDesc(Corp corp,Boolean enabled);

    Optional<FlowComment> findTopByCorpAndEnabledOrderByCreatedAtDesc(Corp corp, boolean b);

    Optional<FlowComment> findTopByCorp(Corp corp);
}
