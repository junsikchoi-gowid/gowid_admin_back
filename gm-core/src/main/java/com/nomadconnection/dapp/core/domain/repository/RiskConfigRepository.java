package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Risk;
import com.nomadconnection.dapp.core.domain.RiskConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiskConfigRepository extends JpaRepository<RiskConfig, Long> {

    Optional<RiskConfig> findByIdxUser(Long idxUser);
}
