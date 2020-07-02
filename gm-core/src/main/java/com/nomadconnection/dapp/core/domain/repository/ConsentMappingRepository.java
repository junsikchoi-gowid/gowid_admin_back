package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Consent;
import com.nomadconnection.dapp.core.domain.ConsentMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface ConsentMappingRepository extends JpaRepository<ConsentMapping, Long> {
    ConsentMapping findTopByIdxUserAndIdxConsentOrderByIdxDesc(Long idxUser, Long idxConsent);
}