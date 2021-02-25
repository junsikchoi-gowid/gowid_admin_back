package com.nomadconnection.dapp.core.domain.repository.limit;

import com.nomadconnection.dapp.core.domain.limit.LimitRecalculationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LimitRecalculationHistoryRepository extends JpaRepository<LimitRecalculationHistory, Long> {
}
