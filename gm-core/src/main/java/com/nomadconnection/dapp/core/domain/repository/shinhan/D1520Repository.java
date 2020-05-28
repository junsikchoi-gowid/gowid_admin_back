package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.D1510;
import com.nomadconnection.dapp.core.domain.D1520;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("Shinhan.D1520Repository")
public interface D1520Repository extends JpaRepository<D1520, Long> {

    D1510 findFirstByD001OrderByCreatedAt(String businessLicenseNo);


}
