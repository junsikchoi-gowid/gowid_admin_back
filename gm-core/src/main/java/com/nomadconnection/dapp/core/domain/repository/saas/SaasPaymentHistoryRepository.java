package com.nomadconnection.dapp.core.domain.repository.saas;

import com.nomadconnection.dapp.core.domain.saas.SaasInfo;
import com.nomadconnection.dapp.core.domain.saas.SaasPaymentHistory;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaasPaymentHistoryRepository extends JpaRepository<SaasPaymentHistory, Long> {

    List<SaasPaymentHistory> findTop5ByUserOrderByPaymentDateDesc(User user);

    List<SaasPaymentHistory> findAllByUserAndPaymentDateBetweenOrderByPaymentDateDesc(User user, String startDate, String endDate);

    List<SaasPaymentHistory> findAllByUserAndSaasInfoOrderByPaymentDateDesc(User user, SaasInfo sassInfo);

    Page<SaasPaymentHistory> findAllByUser(User user, Pageable pageable);

    Page<SaasPaymentHistory> findAllByUserAndPaymentDateBetweenOrderByPaymentDateDesc(User user, String fromPaymentDate, String toPaymentDate, Pageable pageable);

    List<SaasPaymentHistory> findAllByUserAndSaasInfoAndPaymentDateBetweenOrderByPaymentDateDesc(User user, SaasInfo saasInfo, String fromDt, String toDt);

    /**
     * 월별 총 SaaS 결제 금액 조회
     *
     * @param idxUser User Idx
     * @param fromDt  시작년월
     * @param toDt    종료년월
     *
     * @return 월별 지출 금액 목록
     */
    @Query(value = "select left(paymentDate, 6) as pDate, \n" +
                   "       sum(paymentPrice) as pSum \n" +
                   "from SaasPaymentHistory\n" +
                   "where idxUser = :idxUser \n" +
                   "and paymentDate between :fromDt and :toDt \n" +
                   "group by pdate", nativeQuery = true)
    List<UsageSumsDto> getUsageSums(@Param("idxUser") Long idxUser,
                                    @Param("fromDt") String fromDt,
                                    @Param("toDt") String toDt);

    @Query(value = "select left(paymentDate, 6) as pDate, \n" +
                    "       sum(paymentPrice) as pSum \n" +
                    "from SaasPaymentHistory\n" +
                    "where idxUser = :idxUser \n" +
                    "and idxSaasInfo = :idxSaasInfo \n" +
                    "and paymentDate between :fromDt and :toDt \n" +
                    "group by pdate", nativeQuery = true)
    List<UsageSumsDto> getUsageSumsBySaasInfoIdxAll(@Param("idxUser") Long idxUser,
                                                 @Param("idxSaasInfo") Long idxSaasInfo,
                                                 @Param("fromDt") String fromDt,
                                                 @Param("toDt") String toDt);

    @Query(value = "select left(paymentDate, 6) as pDate, \n" +
                    "       sum(paymentPrice) as pSum \n" +
                    "from SaasPaymentHistory\n" +
                    "where idxUser = :idxUser \n" +
                    "and idxSaasInfo = :idxSaasInfo \n" +
                    "and paymentDate between :fromDt and :toDt \n" +
                    "and organization = :organization \n" +
                    "and accountNumber = :accountNumber \n" +
                    "and cardNumber = :cardNumber \n" +
                    "group by pdate", nativeQuery = true)
    List<UsageSumsDto> getUsageSumsBySaasInfoIdx(@Param("idxUser") Long idxUser,
                                                 @Param("idxSaasInfo") Long idxSaasInfo,
                                                 @Param("fromDt") String fromDt,
                                                 @Param("toDt") String toDt,
                                                 @Param("organization") String organization,
                                                 @Param("accountNumber") String accountNumber,
                                                 @Param("cardNumber") String cardNumber);

    @Query(value = "select ROUND(((IFNULL(b.psum, 0) - IFNULL(a.psum, 0)) / IFNULL(a.psum, 0) * 100),2) AS mom\n" +
                    "from (select sum(paymentPrice) psum\n" +
                    "from gowid.SaasPaymentHistory\n" +
                    "where idxUser = :idxUser \n" +
                    "and idxSaasInfo = :idxSaasInfo \n" +
                    "and left(paymentDate, 6) = :beforeDate ) a,\n" +
                    "     (select sum(paymentPrice) psum\n" +
                    "from gowid.SaasPaymentHistory\n" +
                    "where idxUser = :idxUser \n" +
                    "and idxSaasInfo = :idxSaasInfo \n" +
                    "and left(paymentDate, 6) = :searchDate ) b", nativeQuery = true)
    String getPaymentMomAtMonthAll(@Param("idxUser") Long idxUser,
                                @Param("idxSaasInfo") Long idxSaasInfo,
                                @Param("beforeDate") String beforeDate,
                                @Param("searchDate") String searchDate);

    @Query(value = "select ROUND(((IFNULL(b.psum, 0) - IFNULL(a.psum, 0)) / IFNULL(a.psum, 0) * 100),2) AS mom\n" +
                    "from (select sum(paymentPrice) psum\n" +
                    "from gowid.SaasPaymentHistory\n" +
                    "where idxUser = :idxUser \n" +
                    "and idxSaasInfo = :idxSaasInfo \n" +
                    "and organization = :organization \n" +
                    "and accountNumber = :accountNumber \n" +
                    "and cardNumber = :cardNumber \n" +
                    "and left(paymentDate, 6) = :beforeDate ) a,\n" +
                    "     (select sum(paymentPrice) psum\n" +
                    "from gowid.SaasPaymentHistory\n" +
                    "where idxUser = :idxUser \n" +
                    "and idxSaasInfo = :idxSaasInfo \n" +
                    "and organization = :organization \n" +
                    "and accountNumber = :accountNumber \n" +
                    "and cardNumber = :cardNumber \n" +
                    "and left(paymentDate, 6) = :searchDate ) b", nativeQuery = true)
    String getPaymentMomAtMonth(@Param("idxUser") Long idxUser,
                                @Param("idxSaasInfo") Long idxSaasInfo,
                                @Param("beforeDate") String beforeDate,
                                @Param("searchDate") String searchDate,
                                @Param("organization") String organization,
                                @Param("accountNumber") String accountNumber,
                                @Param("cardNumber") String cardNumber);

    public static interface UsageSumsDto {
        String getPDate();
        Long getPSum();
    }

    @Query(value = "select round(sum(paymentPrice) / 3) psumAvg \n" +
                    "from gowid.SaasPaymentHistory \n" +
                    "where idxUser = :idxUser \n" +
                    "and paymentDate between :fromDt and :toDt", nativeQuery = true)
    Long getUsageAvgAtMonth(@Param("idxUser") Long idxUser,
                            @Param("fromDt") String fromDt,
                            @Param("toDt") String toDt);

    /**
     * 해당 월 SaaS 결제 금액 조회
     *
     * @param idxUser User Idx
     * @param fromDt 시작년월
     * @param toDt   종료년월
     * @return 해당 월 SaaS 결제 금액 목록
     */
    @Query(value = "select info.name as name, \n" +
                   "       info.idx as idxSaasInfo, \n" +
                   "       sum(hist.paymentPrice) as price \n" +
                   "from SaasPaymentHistory hist\n" +
                   "join SaasInfo info\n" +
                   "on info.idx = hist.idxSaasInfo\n" +
                   "where idxUser = :idxUser \n" +
                   "and paymentDate between :fromDt and :toDt \n" +
                   "group by idxSaasInfo \n" +
                   "order by price desc", nativeQuery = true)
    List<UsageSumsDetailsDto> getUsageSumsDetails(@Param("idxUser") Long idxUser,
                                                @Param("fromDt") String fromDt,
                                                @Param("toDt") String toDt);

    interface UsageSumsDetailsDto {
        String getName();
        Long getIdxSaasInfo();
        Long getPrice();
    }

    /**
     *
     *
     * @param idxUser User Idx
     * @param fromDt  시작년월
     * @param toDt    종료년월
     *
     * @return 월별 지출 금액 목록
     */
    @Query(value = "SELECT \n" +
                    "    idxSaasCategory,\n" +
                    "    categoryName,\n" +
                    "    SUM(paymentPrice) AS categorySum,\n" +
                    "    ROUND((SUM(paymentPrice) / totalSum) * 100, 2) AS percent,\n" +
                    "    totalSum\n" +
                    "FROM\n" +
                    "    SaasPaymentHistory hist\n" +
                    "        JOIN\n" +
                    "    (SELECT \n" +
                    "        idxSaasCategory,\n" +
                    "            category.name AS categoryName,\n" +
                    "            info.idx,\n" +
                    "            info.name AS saasName\n" +
                    "    FROM\n" +
                    "        SaasInfo info\n" +
                    "    JOIN SaasCategory category ON info.idxSaasCategory = category.idx) sass ON hist.idxSaasInfo = sass.idx,\n" +
                    "    (SELECT \n" +
                    "        SUM(paymentPrice) AS totalSum\n" +
                    "    FROM\n" +
                    "        SaasPaymentHistory\n" +
                    "    WHERE\n" +
                    "        idxUser = :idxUser \n" +
                    "            AND paymentDate BETWEEN :fromDt AND :toDt) AS total \n" +
                    "WHERE\n" +
                    "    idxUser = :idxUser \n" +
                    "        AND paymentDate BETWEEN :fromDt AND :toDt \n" +
                    "GROUP BY idxSaasCategory , categoryName", nativeQuery = true)
    List<UsageCategoriesDto> getUsageCategories(@Param("idxUser") Long idxUser,
                                          @Param("fromDt") String fromDt,
                                          @Param("toDt") String toDt);

    interface UsageCategoriesDto {
        Long getIdxSaasCategory();
        String getCategoryName();
        Long getCategorySum();
        Double getPercent();
        Long getTotalSum();
    }

    @Query(value = "SELECT \n" +
                    "    info.idx idxSaasInfo, info.name, SUM(hist.paymentPrice) as price \n" +
                    "FROM\n" +
                    "    SaasPaymentHistory hist\n" +
                    "        JOIN\n" +
                    "    SaasInfo info ON hist.idxSaasInfo = info.idx\n" +
                    "WHERE\n" +
                    "    idxUser = :idxUser \n" +
                    "        AND idxSaasInfo IN (SELECT \n" +
                    "            idx\n" +
                    "        FROM\n" +
                    "            SaasInfo\n" +
                    "        WHERE\n" +
                    "            idxSaasCategory = :idxSaasCategory )\n" +
                    "        AND paymentDate BETWEEN :fromDt AND :toDt \n " +
                    "GROUP BY info.name", nativeQuery = true)
    List<UsageSumsDetailsDto> getUsageCategoriesDetails(@Param("idxUser") Long idxUser,
                                                        @Param("idxSaasCategory") Long idxSaasCategory,
                                                        @Param("fromDt") String fromDt,
                                                        @Param("toDt") String toDt);

    @Query(value = "SELECT \n" +
                    "    info.name,\n" +
                    "    hist.idxSaasInfo,\n" +
                    "    IFNULL(a.psum, 0) AS asum,\n" +
                    "    IFNULL(b.psum, 0) AS bsum,\n" +
                    "    ROUND(((IFNULL(b.psum, 0) - IFNULL(a.psum, 0)) / IFNULL(a.psum, 0) * 100),\n" +
                    "            2) AS mom\n" +
                    "FROM\n" +
                    "    SaasPaymentHistory hist\n" +
                    "        LEFT JOIN\n" +
                    "    SaasInfo info ON hist.idxSaasInfo = info.idx\n" +
                    "        LEFT JOIN\n" +
                    "    (SELECT \n" +
                    "        idxSaasInfo, SUM(paymentPrice) psum\n" +
                    "    FROM\n" +
                    "        SaasPaymentHistory\n" +
                    "    WHERE\n" +
                    "        idxUser = :idxUser \n" +
                    "            AND paymentDate BETWEEN DATE_FORMAT(date_add(date_add(LAST_DAY(curdate()),interval 1 DAY),interval -3 MONTH), '%Y%m%d') AND DATE_FORMAT(date_add(LAST_DAY(curdate()),interval -2 MONTH), '%Y%m%d')\n" +
                    "    GROUP BY idxSaasInfo) a ON hist.idxSaasInfo = a.idxSaasInfo\n" +
                    "        LEFT JOIN\n" +
                    "    (SELECT \n" +
                    "        idxSaasInfo, SUM(paymentPrice) psum\n" +
                    "    FROM\n" +
                    "        SaasPaymentHistory\n" +
                    "    WHERE\n" +
                    "        idxUser = :idxUser \n" +
                    "            AND paymentDate BETWEEN DATE_FORMAT(date_add(date_add(LAST_DAY(curdate()),interval 1 DAY),interval -2 MONTH), '%Y%m%d') AND DATE_FORMAT(date_add(LAST_DAY(curdate()),interval -1 MONTH), '%Y%m%d')\n" +
                    "    GROUP BY idxSaasInfo) b ON hist.idxSaasInfo = b.idxSaasInfo\n" +
                    "WHERE\n" +
                    "    idxUser = :idxUser AND b.psum IS NOT NULL\n" +
                    "GROUP BY hist.idxSaasInfo", nativeQuery = true)
    List<BestPaymentTop5Dto> getBestPaymentTop5(@Param("idxUser") Long idxUser);
    interface BestPaymentTop5Dto {
        Long getIdxSaasInfo();
        String getName();
        Long getbSum();
        Long getaSum();
        Double getMom();
    }

    @Query(value = "SELECT \n" +
                    "    pi.idxSaasInfo, info.name, COUNT(pi.num) AS count\n" +
                    "FROM\n" +
                    "    (SELECT \n" +
                    "        idxUser,\n" +
                    "            idxSaasInfo,\n" +
                    "            currentPaymentDate,\n" +
                    "            CONCAT(idxSaasInfo, accountNumber, cardNumber) AS num\n" +
                    "    FROM\n" +
                    "        SaasPaymentInfo\n" +
                    "    WHERE\n" +
                    "        idxUser = :idxUser AND isDup = TRUE\n" +
                    "            AND currentPaymentDate BETWEEN DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL - 3 MONTH), '%Y%m%d') AND DATE_FORMAT(CURDATE(), '%Y%m%d')\n" +
                    "    GROUP BY CONCAT(idxSaasInfo, accountNumber, cardNumber)) pi\n" +
                    "        LEFT JOIN\n" +
                    "    SaasInfo info ON pi.idxSaasInfo = info.idx\n" +
                    "GROUP BY pi.idxSaasInfo\n" +
                    "HAVING count >= 3\n" +
                    "ORDER BY count DESC\n" +
                    "LIMIT 5", nativeQuery = true)
    List<DuplicatePaymentDto> getDuplicatePaymentList(@Param("idxUser") Long idxUser);
    interface DuplicatePaymentDto {
        Long getIdxSaasInfo();
        String getName();
        Integer getCount();
    }

    @Query(value = "SELECT \n" +
                    "    a.*" +
                    "FROM\n" +
                    "    (SELECT \n" +
                    "        MIN(paymentDate) paymentDate, idxSaasInfo\n" +
                    "    FROM\n" +
                    "        SaasPaymentHistory\n" +
                    "    WHERE\n" +
                    "        idxUser = :idxUser \n" +
                    "    GROUP BY idxSaasInfo) t1\n" +
                    "        INNER JOIN\n" +
                    "    SaasPaymentHistory a ON a.idxSaasInfo = t1.idxSaasInfo\n" +
                    "        AND a.paymentDate = t1.paymentDate\n" +
                    "GROUP BY paymentDate\n" +
                    "ORDER BY paymentDate DESC\n" +
                    "LIMIT 5", nativeQuery = true)
    List<SaasPaymentHistory> findTop5ByUserIsNew(@Param("idxUser") Long idxUser);
}
