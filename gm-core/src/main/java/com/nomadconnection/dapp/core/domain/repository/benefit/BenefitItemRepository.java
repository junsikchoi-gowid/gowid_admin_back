package com.nomadconnection.dapp.core.domain.repository.benefit;

import com.nomadconnection.dapp.core.domain.benefit.Benefit;
import com.nomadconnection.dapp.core.domain.benefit.BenefitItem;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BenefitItemRepository extends JpaRepository<BenefitItem, Long> {

    // Benefit에 해당하는 Item 삭제
    void deleteAllByBenefit(Benefit benefit);

}
