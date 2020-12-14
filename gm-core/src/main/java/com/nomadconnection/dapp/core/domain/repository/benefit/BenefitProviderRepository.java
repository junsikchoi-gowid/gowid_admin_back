package com.nomadconnection.dapp.core.domain.repository.benefit;

import com.nomadconnection.dapp.core.domain.benefit.Benefit;
import com.nomadconnection.dapp.core.domain.benefit.BenefitProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitProviderRepository extends JpaRepository<BenefitProvider, Long> {

    // Benefit에 해당하는 Provider 삭제
    void deleteAllByBenefit(Benefit benefit);
}
