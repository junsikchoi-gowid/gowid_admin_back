package com.nomadconnection.dapp.core.domain.repository.consent;

import com.nomadconnection.dapp.core.domain.consent.ConsentMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ConsentMappingRepository extends JpaRepository<ConsentMapping, Long> {
    ConsentMapping findTopByIdxUserAndIdxConsentOrderByIdxDesc(Long idxUser, Long idxConsent);

    List<ConsentMapping> findAllByIdxUser(Long idxUser);

    @Transactional
    @Modifying
    @Query("delete from ConsentMapping where idxUser = :idxUser and idxConset not in (select idx from Consent where enable = 1 and typeCode = 'GOWID-A')")
    void deleteAllByUserIdx(@Param("idxUser") Long idxUser);
}