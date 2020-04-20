package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.RiskConfig;
import com.nomadconnection.dapp.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiskConfigRepository extends JpaRepository<RiskConfig, Long> {

    Optional<RiskConfig> findByUserAndEnabled(User user, boolean enabled);


    Optional<RiskConfig> findByCorpAndEnabled(Corp corp, boolean enabled);
}
