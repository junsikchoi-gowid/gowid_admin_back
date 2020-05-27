package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.D1200;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface D1200Repository extends JpaRepository<Long, D1200> {

    D1200 findFirstByD001OrderByCreatedAtDesc(String businessLicenseNo);

}
