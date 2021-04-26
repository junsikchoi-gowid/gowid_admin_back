package com.nomadconnection.dapp.core.domain.repository.saas;

import com.nomadconnection.dapp.core.domain.saas.SaasInfo;
import com.nomadconnection.dapp.core.domain.saas.SaasPaymentInfo;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaasPaymentInfoRepository extends JpaRepository<SaasPaymentInfo, Long> {

    List<SaasPaymentInfo> findAllByUser(User user);

    List<SaasPaymentInfo> findAllByUserAndActiveSubscriptionIsTrue(User user);

    List<SaasPaymentInfo> findAllByUserAndActiveSubscriptionIsTrueOrderByPaymentScheduleDateAsc(User user);

    List<SaasPaymentInfo> findAllByUserAndSaasInfo(User user, SaasInfo saasInfo);

    List<SaasPaymentInfo> findAllByUserAndSaasInfoOrderByDisabledAscCurrentPaymentDateDesc(User user, SaasInfo saasInfo);

    @Query(value = "SELECT \n" +
            "    *\n" +
            "FROM\n" +
            "    SaasPaymentInfo\n" +
            "WHERE\n" +
            "    idxUser = :idxUser \n" +
            "        and disabled = false\n" +
            "        AND paymentScheduleDate >= NOW()\n" +
            "ORDER BY paymentScheduleDate\n" +
            "LIMIT 5", nativeQuery = true)
    List<SaasPaymentInfo> findAllByUserScheduleList(@Param("idxUser") Long idxUser);

    @Query(value = "SELECT \n" +
            "    paymentInfo.idxSaasInfo,\n" +
            "    MAX(paymentInfo.currentPaymentDate) currentPaymentDate,\n" +
            "    paymentInfo.currentPaymentPrice,\n" +
            "    paymentInfo.paymentMethod,\n" +
            "    manage.activeAlert,\n" +
            "    paymentInfo.activeSubscription,\n" +
            "    manage.managerName,\n" +
            "    manage.managerEmail,\n" +
            "    info.name saasName,\n" +
            "    info.imageName saasImageName,\n" +
            "    category.name categoryName,\n" +
            "    lastPayment.lastMonthPaymentPrice, \n" +
            "    paymentInfo.isNew \n" +
            "FROM\n" +
            "    SaasPaymentInfo paymentInfo\n" +
            "        JOIN\n" +
            "    SaasInfo info ON paymentInfo.idxSaasInfo = info.idx\n" +
            "        JOIN\n" +
            "    SaasCategory category ON info.idxSaasCategory = category.idx\n" +
            "        LEFT JOIN\n" +
            "    SaasPaymentManageInfo manage ON paymentInfo.idxSaasPaymentManageInfo = manage.idx\n" +
            "        LEFT JOIN\n" +
            "    (select idxSaasInfo, sum(paymentPrice) lastMonthPaymentPrice from gowid.SaasPaymentHistory\n" +
            "        where paymentDate between :fromDt and :toDt \n" +
            "        and idxUser = :idxUser\n" +
            "        group by idxSaasInfo) lastPayment on paymentInfo.idxSaasInfo = lastPayment.idxSaasInfo\n" +"WHERE\n" +
            "    paymentInfo.idxUser = :idxUser \n" +
            "        AND activeSubscription = TRUE\n" +
            "GROUP BY idxSaasInfo\n" +
            "ORDER BY saasName", nativeQuery = true)
    List<SubscriptSaasDto> findAllSubscriptionByUser(@Param("idxUser") Long idxUser,
                                                     @Param("fromDt") String fromDt,
                                                     @Param("toDt") String toDt);

    @Query(value = "SELECT \n" +
            "    paymentInfo.idxSaasInfo,\n" +
            "    paymentInfo.currentPaymentDate currentPaymentDate,\n" +
            "    paymentInfo.currentPaymentPrice,\n" +
            "    paymentInfo.paymentMethod,\n" +
            "    manage.activeAlert,\n" +
            "    paymentInfo.activeSubscription,\n" +
            "    manage.managerName,\n" +
            "    manage.managerEmail,\n" +
            "    info.name saasName,\n" +
            "    info.imageName saasImageName,\n" +
            "    category.name categoryName\n" +
            "FROM\n" +
            "    SaasPaymentInfo paymentInfo\n" +
            "        JOIN\n" +
            "    SaasInfo info ON paymentInfo.idxSaasInfo = info.idx\n" +
            "        JOIN\n" +
            "    SaasCategory category ON info.idxSaasCategory = category.idx\n" +
            "        LEFT JOIN\n" +
            "    SaasPaymentManageInfo manage ON paymentInfo.idxSaasPaymentManageInfo = manage.idx\n" +
            "WHERE\n" +
            "    paymentInfo.idxUser = :idxUser \n" +
            "        AND activeSubscription = FALSE\n" +
            "        AND idxSaasInfo NOT IN (SELECT \n" +
            "            idxSaasInfo\n" +
            "        FROM\n" +
            "            SaasPaymentInfo\n" +
            "        WHERE\n" +
            "            idxUser = :idxUser \n" +
            "                AND activeSubscription = TRUE)\n" +
            "GROUP BY idxSaasInfo", nativeQuery = true)
    List<SubscriptSaasDto> findAllUnsubscriptionByUser(@Param("idxUser") Long idxUser);

    @Query(value = "select round(sum(currentPaymentPrice)) psum \n" +
                    "from gowid.SaasPaymentInfo \n" +
                    "where idxUser = :idxUser \n" +
                    "and paymentType in (2, 5) \n" +
                    "and paymentScheduleDate between :fromDate and :toDate", nativeQuery = true)
    Long getScheduledPriceSumAtMonth(@Param("idxUser") Long idxUser,
                                    @Param("fromDate") String fromDt,
                                    @Param("toDate") String toDt);

//    Optional<String> findByUserAndSaasInfoAndOrganizationAndAccountNumberAndCardNumberEndWith(User user, SaasInfo saasInfo, String organization, String accountNumber, String cardNumber);

    Optional<SaasPaymentInfo> findByUserAndSaasInfoAndOrganizationAndAccountNumberAndCardNumberContains(User user, SaasInfo saasInfo, String organization, String accountNumber, String cardNumber);

    interface SubscriptSaasDto {
        Long getIdxSaasInfo();
        String getCurrentPaymentDate();
        Long getCurrentPaymentPrice();
        Integer getPaymentMethod();
        Integer getPaymentType();
        Boolean getActiveAlert();
        Boolean getActiveSubscription();
        String getManagerName();
        String getManagerEmail();
        String getSaasName();
        String getSaasImageName();
        String getCategoryName();
        Long getLastMonthPaymentPrice();
        Boolean getIsNew();
    }

    @Query(value = "SELECT \n" +
            "    paymentType\n" +
            "FROM\n" +
            "    SaasPaymentInfo\n" +
            "WHERE\n" +
            "    idxUser = :idxUser AND idxSaasInfo = :idxSaasInfo \n" +
            "        AND activeSubscription = :isSubscription \n" +
            "        AND disabled = false \n" +
            "GROUP BY paymentType", nativeQuery = true)
    List<Integer> findPaymentType(@Param("idxUser") Long idxUser,
                                  @Param("idxSaasInfo") Long idxSaasInfo,
                                  @Param("isSubscription") Boolean isSubscription);


    Page<SaasPaymentInfo> findAllByUser(User user, Pageable pageable);
}
