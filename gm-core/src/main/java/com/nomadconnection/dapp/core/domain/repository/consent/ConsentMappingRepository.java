package com.nomadconnection.dapp.core.domain.repository.consent;

import com.nomadconnection.dapp.core.domain.consent.ConsentMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsentMappingRepository extends JpaRepository<ConsentMapping, Long> {
    ConsentMapping findTopByIdxUserAndIdxConsentOrderByIdxDesc(Long idxUser, Long idxConsent);
}