package com.nomadconnection.dapp.core.domain.repository.saas;

import com.nomadconnection.dapp.core.domain.saas.SaasPaymentManageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaasPaymentManageInfoRepository extends JpaRepository<SaasPaymentManageInfo, Long> {
}
