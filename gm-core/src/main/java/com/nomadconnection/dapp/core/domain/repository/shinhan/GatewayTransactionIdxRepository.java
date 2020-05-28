package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.GatewayTransactionIdx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayTransactionIdxRepository extends JpaRepository<GatewayTransactionIdx, Long> {

}
