package com.nomadconnection.dapp.core.domain.repository.benefit;

import com.nomadconnection.dapp.core.domain.benefit.Benefit;
import com.nomadconnection.dapp.core.domain.benefit.BenefitItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitItemRepository extends JpaRepository<BenefitItem, Long> {

    // Benefit에 해당하는 Item 삭제
    void deleteAllByBenefit(Benefit benefit);

}
