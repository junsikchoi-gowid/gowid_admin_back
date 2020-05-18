package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.Dept;
import com.nomadconnection.dapp.core.domain.RiskConfig;
import com.nomadconnection.dapp.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiskConfigRepository extends JpaRepository<RiskConfig, Long> {

    Optional<RiskConfig> findByUserAndEnabled(User user, boolean enabled);

    Optional<RiskConfig> findByCorpAndEnabled(Corp corp, boolean enabled);

    @Modifying
    @Query(value = "UPDATE RiskConfig r SET r.idxUser = :idxUser, r.idxCorp = :idxCorp WHERE r.idx = :idx", nativeQuery = true)
    void modifyRiskConfig(Long idx, Long idxUser, Long idxCorp);
}
