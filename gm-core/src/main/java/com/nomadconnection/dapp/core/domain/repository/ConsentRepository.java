package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
	List<Consent> findByIdxIn(List<Long> idx);

	Stream<Consent> findAllByEnabledOrderByConsentOrderAsc(Boolean enabled);

	@Modifying
	@Query("UPDATE ConsentMapping u SET u.status = :status WHERE u.idxUser = :idxuser and u.idxConsent = :idxconsent")
	int updateConsentMapping(@Param("status") boolean status,
							 @Param("idxuser") Long idxuser,
							 @Param("idxconsent") Long idxconsent);
}