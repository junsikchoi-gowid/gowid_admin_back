package com.nomadconnection.dapp.core.domain.repository.saas;

import com.nomadconnection.dapp.core.domain.saas.SaasCheckCategory;
import com.nomadconnection.dapp.core.domain.saas.SaasCheckInfo;
import com.nomadconnection.dapp.core.domain.saas.SaasPaymentInfo;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaasCheckInfoRepository extends JpaRepository<SaasCheckInfo, Long> {

    /**
     * 체크리스트 목록 조회
     *
     * @param user
     * @return
     */
    List<SaasCheckInfo> findAllByUserAndCheckedFalseOrderByCreatedAt(User user);

    /**
     * 체크리스트 데이터 조회
     *
     * @param user
     * @param idxSaasCheckInfo
     * @return
     */
    Optional<SaasCheckInfo> findByUserAndIdx(User user, Long idxSaasCheckInfo);


    /**
     * 체크리스트 항목 삭제
     *
     * @param checkCategory
     * @param paymentInfo
     */
    void deleteBySaasCheckCategoryAndSaasPaymentInfo(SaasCheckCategory checkCategory, SaasPaymentInfo paymentInfo);
}
