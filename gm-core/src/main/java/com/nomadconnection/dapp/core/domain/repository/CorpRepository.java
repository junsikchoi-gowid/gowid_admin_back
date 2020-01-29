package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Corp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorpRepository extends JpaRepository<Corp, Long> {

	// Optional<Corp> findByBizRegNo(String bizRegNo);
	// Optional<Corp> findByNameAndBizRegNo(String name, String bizRegNo);
	Optional<Corp> findByResCompanyIdentityNo(String resCompanyIdentityNo);
}
