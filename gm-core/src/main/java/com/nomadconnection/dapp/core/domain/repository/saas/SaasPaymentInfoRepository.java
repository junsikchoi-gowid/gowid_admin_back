package com.nomadconnection.dapp.core.domain.repository.saas;

import com.nomadconnection.dapp.core.domain.saas.SaasInfo;
import com.nomadconnection.dapp.core.domain.saas.SaasPaymentInfo;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaasPaymentInfoRepository extends JpaRepository<SaasPaymentInfo, Long> {

    List<SaasPaymentInfo> findAllByUser(User user);

    List<SaasPaymentInfo> findAllByUserAndActiveSubscriptionIsTrue(User user);

    List<SaasPaymentInfo> findAllByUserAndSaasInfo(User user, SaasInfo sassInfo);

    List<SaasPaymentInfo> findTop5ByUserAndIsNewTrueOrderByCurrentPaymentDateDesc(User user);

}
