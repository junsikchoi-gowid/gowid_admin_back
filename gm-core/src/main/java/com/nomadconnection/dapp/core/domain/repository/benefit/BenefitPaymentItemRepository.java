package com.nomadconnection.dapp.core.domain.repository.benefit;

import com.nomadconnection.dapp.core.domain.benefit.BenefitPaymentHistory;
import com.nomadconnection.dapp.core.domain.benefit.BenefitPaymentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitPaymentItemRepository extends JpaRepository<BenefitPaymentItem, Long> {

}
