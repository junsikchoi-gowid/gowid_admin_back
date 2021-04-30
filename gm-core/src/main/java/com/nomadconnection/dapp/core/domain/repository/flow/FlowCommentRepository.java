package com.nomadconnection.dapp.core.domain.repository.flow;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.flow.FlowComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FlowCommentRepository extends JpaRepository<FlowComment, Long> {


    List<FlowComment> findByCorpAndEnabledOrderByCreatedAtDesc(Corp corp,Boolean enabled);

    Optional<FlowComment> findTopByCorpAndEnabledAndReadYnOrderByCreatedAtDesc(Corp corp, boolean b, boolean b1);

    int countByCorpAndEnabledAndReadYn(Corp corp, boolean b, boolean b1);
}
