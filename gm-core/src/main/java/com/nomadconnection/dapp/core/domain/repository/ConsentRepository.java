package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.CardIssuance;
import com.nomadconnection.dapp.core.domain.Consent;
import com.nomadconnection.dapp.core.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
	List<Consent> findByIdxIn(List<Long> idx);

	Page<Consent> findBy(Consent consent, Pageable pageable);

	@Modifying
	@Query("UPDATE ConsentMapping u SET u.status = :status WHERE u.idxUser = :idxuser and u.idxConsent = :idxconsent")
	int updateConsentMapping(@Param("status") boolean status,
							 @Param("idxuser") Long idxuser,
							 @Param("idxconsent") Long idxconsent);
}