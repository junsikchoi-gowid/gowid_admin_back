package com.nomadconnection.dapp.core.domain.repository.benefit;

import com.nomadconnection.dapp.core.domain.benefit.Benefit;
import com.nomadconnection.dapp.core.domain.benefit.BenefitPaymentHistory;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, Long> {

	Page<Benefit> findAllByDisabledFalseOrderByPriority(Pageable pageable);
}
