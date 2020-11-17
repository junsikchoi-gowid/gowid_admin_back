package com.nomadconnection.dapp.core.domain.repository.benefit;

import com.nomadconnection.dapp.core.domain.benefit.BenefitSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitSearchHistoryRepository extends JpaRepository<BenefitSearchHistory, Long> {

}
