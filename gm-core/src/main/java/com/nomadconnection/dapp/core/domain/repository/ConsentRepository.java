package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
	List<Consent> findByIdxIn(List<Long> idx);

	@Modifying
	@Query("UPDATE ConsentMapping u SET u.Status = :status WHERE u.idxUser = :idxUser and u.idxConsent = :idxConsent")
	int updateConsentMapping(@Param("status") Integer status,
							 @Param("idxUser") Long idxUser,
							 @Param("idxConsent") Long idxConsent);
}