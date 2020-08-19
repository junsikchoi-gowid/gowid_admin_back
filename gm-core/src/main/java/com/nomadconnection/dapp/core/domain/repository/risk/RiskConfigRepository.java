package com.nomadconnection.dapp.core.domain.repository.risk;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RiskConfigRepository extends JpaRepository<RiskConfig, Long> {

    Optional<RiskConfig> findByUserAndEnabled(User user, boolean isEnabled);

    Optional<RiskConfig> findByCorpAndEnabled(Corp corp, boolean enabled);

    RiskConfig getTopByCorpAndEnabled(Corp corp, boolean enabled);

    @Modifying
    @Query(value = "UPDATE RiskConfig r SET r.idxUser = :idxUser, r.idxCorp = :idxCorp WHERE r.idx = :idx", nativeQuery = true)
    void modifyRiskConfig(Long idx, Long idxUser, Long idxCorp);

    @Transactional
    @Modifying
    @Query("delete from RiskConfig  where idxCorp = :idxCorp")
    void deleteByCorpIdx(@Param("idxCorp") Long idxCorp);
}
