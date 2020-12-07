package com.nomadconnection.dapp.core.domain.repository.etc;

import com.nomadconnection.dapp.core.domain.etc.LimitInInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LimitInquiryRepository extends JpaRepository<LimitInInquiry, Long> {

}
