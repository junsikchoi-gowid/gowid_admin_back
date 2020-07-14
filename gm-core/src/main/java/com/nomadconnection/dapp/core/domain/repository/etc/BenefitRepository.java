package com.nomadconnection.dapp.core.domain.repository.etc;

import com.nomadconnection.dapp.core.domain.etc.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, Long> {
	List<Benefit> findAllByDisabledFalse();
}
