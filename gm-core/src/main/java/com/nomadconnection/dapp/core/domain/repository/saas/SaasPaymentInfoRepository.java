package com.nomadconnection.dapp.core.domain.repository.saas;

import com.nomadconnection.dapp.core.domain.saas.SaasInfo;
import com.nomadconnection.dapp.core.domain.saas.SaasPaymentInfo;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaasPaymentInfoRepository extends JpaRepository<SaasPaymentInfo, Long> {

    List<SaasPaymentInfo> findAllByUser(User user);

    List<SaasPaymentInfo> findAllByUserAndActiveSubscriptionIsTrue(User user);

    List<SaasPaymentInfo> findAllByUserAndActiveSubscriptionIsTrueOrderByPaymentScheduleDateAsc(User user);

    List<SaasPaymentInfo> findAllByUserAndSaasInfo(User user, SaasInfo saasInfo);

    @Query(value = "SELECT \n" +
            "    *\n" +
            "FROM\n" +
            "    SaasPaymentInfo\n" +
            "WHERE\n" +
            "    idxUser = :idxUser \n" +
            "        AND paymentScheduleDate >= NOW()\n" +
            "ORDER BY paymentScheduleDate\n" +
            "LIMIT 3", nativeQuery = true)
    List<SaasPaymentInfo> findAllByUserScheduleList(@Param("idxUser") Long idxUser);

    @Query(value = "SELECT \n" +
            "    idxSaasInfo,\n" +
            "    currentPaymentDate,\n" +
            "    currentPaymentPrice,\n" +
            "    paymentMethod,\n" +
            "    paymentType,\n" +
            "    activeAlert,\n" +
            "    activeSubscription,\n" +
            "    managerName,\n" +
            "    managerEmail,\n" +
            "    saasName,\n" +
            "    saasImageName,\n" +
            "    categoryName\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        paymentInfo.idxSaasInfo,\n" +
            "        MAX(paymentInfo.currentPaymentDate) currentPaymentDate,\n" +
            "        paymentInfo.currentPaymentPrice,\n" +
            "        paymentInfo.paymentMethod,\n" +
            "        paymentInfo.paymentType,\n" +
            "        manage.activeAlert,\n" +
            "        paymentInfo.activeSubscription,\n" +
            "        manage.managerName,\n" +
            "        manage.managerEmail,\n" +
            "        info.name saasName,\n" +
            "        info.imageName saasImageName,\n" +
            "        category.name categoryName\n" +
            "    FROM\n" +
            "        SaasPaymentInfo paymentInfo\n" +
            "    JOIN SaasPaymentManageInfo manage ON paymentInfo.idxSaasPaymentManageInfo = manage.idx\n" +
            "    JOIN SaasInfo info ON paymentInfo.idxSaasInfo = info.idx\n" +
            "    JOIN SaasCategory category ON info.idxSaasCategory = category.idx\n" +
            "    WHERE\n" +
            "        paymentInfo.idxUser = :idxUser \n" +
            "            AND activeSubscription = TRUE\n" +
            "    GROUP BY idxSaasInfo UNION ALL " +
            "SELECT \n" +
            "        paymentInfo.idxSaasInfo,\n" +
            "        paymentInfo.currentPaymentDate currentPaymentDate,\n" +
            "        paymentInfo.currentPaymentPrice,\n" +
            "        paymentInfo.paymentMethod,\n" +
            "        paymentInfo.paymentType,\n" +
            "        manage.activeAlert,\n" +
            "        paymentInfo.activeSubscription,\n" +
            "        manage.managerName,\n" +
            "        manage.managerEmail,\n" +
            "        info.name saasName,\n" +
            "        info.imageName saasImageName,\n" +
            "        category.name categoryName\n" +
            "    FROM\n" +
            "        SaasPaymentInfo paymentInfo\n" +
            "    JOIN SaasPaymentManageInfo manage ON paymentInfo.idxSaasPaymentManageInfo = manage.idx\n" +
            "    JOIN SaasInfo info ON paymentInfo.idxSaasInfo = info.idx\n" +
            "    JOIN SaasCategory category ON info.idxSaasCategory = category.idx\n" +
            "    WHERE\n" +
            "        paymentInfo.idxUser = :idxUser \n" +
            "            AND activeSubscription = FALSE\n" +
            "            AND idxSaasInfo NOT IN (SELECT \n" +
            "                idxSaasInfo\n" +
            "            FROM\n" +
            "                SaasPaymentInfo\n" +
            "            WHERE\n" +
            "                idxUser = :idxUser \n" +
            "                    AND activeSubscription = TRUE)\n" +
            "    GROUP BY idxSaasInfo) a", nativeQuery = true)
    List<SubscriptSaasDto> findAllPaymentInfoByUser(@Param("idxUser") Long idxUser);

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
            "    category.name categoryName\n" +
            "FROM\n" +
            "    SaasPaymentInfo paymentInfo\n" +
            "        JOIN\n" +
            "    SaasPaymentManageInfo manage ON paymentInfo.idxSaasPaymentManageInfo = manage.idx\n" +
            "        JOIN\n" +
            "    SaasInfo info ON paymentInfo.idxSaasInfo = info.idx\n" +
            "        JOIN\n" +
            "    SaasCategory category ON info.idxSaasCategory = category.idx\n" +
            "WHERE\n" +
            "    paymentInfo.idxUser = :idxUser \n" +
            "        AND activeSubscription = TRUE\n" +
            "GROUP BY idxSaasInfo\n" +
            "ORDER BY saasName", nativeQuery = true)
    List<SubscriptSaasDto> findAllSubscriptionByUser(@Param("idxUser") Long idxUser);

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
            "    SaasPaymentManageInfo manage ON paymentInfo.idxSaasPaymentManageInfo = manage.idx\n" +
            "        JOIN\n" +
            "    SaasInfo info ON paymentInfo.idxSaasInfo = info.idx\n" +
            "        JOIN\n" +
            "    SaasCategory category ON info.idxSaasCategory = category.idx\n" +
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

    @Query(value = "SELECT \n" +
            "    paymentType\n" +
            "FROM\n" +
            "    SaasPaymentInfo\n" +
            "WHERE\n" +
            "    idxUser = :idxUser AND idxSaasInfo = :idxSaasInfo \n" +
            "        AND activeSubscription = :isSubscription \n" +
            "GROUP BY paymentType", nativeQuery = true)
    List<Integer> findPaymentType(@Param("idxUser") Long idxUser,
                                  @Param("idxSaasInfo") Long idxSaasInfo,
                                  @Param("isSubscription") Boolean isSubscription);

    public static interface SubscriptSaasDto {
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
    }

    @Query(value = "SELECT \n" +
            "    COUNT(*)\n" +
            "FROM\n" +
            "    SaasPaymentInfo\n" +
            "WHERE\n" +
            "    idxUser = :idxUser AND idxSaasInfo = :idxSaasInfo \n" +
            "        AND activeSubscription = TRUE", nativeQuery = true)
    Integer findSaasSubscriptionByUserAndSaasInfo(@Param("idxUser") Long idxUser,
                                                  @Param("idxSaasInfo") Long idxSaasInfo);
}
