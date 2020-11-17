package com.nomadconnection.dapp.core.domain.repository.benefit;

import com.nomadconnection.dapp.core.domain.benefit.BenefitPaymentHistory;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitPaymentHistoryRepository extends JpaRepository<BenefitPaymentHistory, Long> {

    Page<BenefitPaymentHistory> findAllByUserAndStatus(User user, Pageable pageable, String status);

}