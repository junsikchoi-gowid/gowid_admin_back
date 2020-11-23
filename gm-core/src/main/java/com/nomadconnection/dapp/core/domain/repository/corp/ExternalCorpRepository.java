package com.nomadconnection.dapp.core.domain.repository.corp;

import com.nomadconnection.dapp.core.domain.corp.ExternalCorp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExternalCorpRepository extends JpaRepository<ExternalCorp, Long> {
    /**
     * @param externalId 외부에 공개되는 아이디
     * @return
     */
    ExternalCorp findByExternalId(String externalId);
}
