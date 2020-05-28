package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.D1510;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("Shinhan.D1510Repository")
public interface D1510Repository extends JpaRepository<D1510, Long> {

    D1510 findFirstByD003OrderByCreatedAt(String businessLicenseNo);

}
