package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.D1200;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("Shinhan.D1200Repository")
public interface D1200Repository extends JpaRepository<D1200, Long> {

    D1200 findFirstByD001OrderByCreatedAtDesc(String businessLicenseNo);

}
