package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
	List<Consent> findByVersionAndEssential(String consentVersion, boolean b);
}