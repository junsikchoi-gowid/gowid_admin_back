package com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo;

import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.IssuanceStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CardIssunaceInfoCustomRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CardIssuanceInfoRepository extends JpaRepository<CardIssuanceInfo, Long>, CardIssunaceInfoCustomRepository {
    Optional<CardIssuanceInfo> findByCorpAndCardType(Corp corp, CardType cardType);
    Optional<CardIssuanceInfo> findByIdx(Long idx);
    Optional<CardIssuanceInfo> findByUserAndCardType(User user, CardType cardType);
    Optional<List<CardIssuanceInfo>> findAllByUser(User user);

    @Query(value = "select idx FROM CardIssuanceInfo where idxUser = :idxUser and disabled = false", nativeQuery = true)
    List<Long> findAllIdxByUserIdx(@Param("idxUser") Long idxUser);

    @Query(value = "select * FROM CardIssuanceInfo where idxUser = :idxUser and disabled = false order by idx", nativeQuery = true)
    Optional<CardIssuanceInfo> findTopByUserAndDisabledFalseOrderByIdxDesc(@Param("idxUser") Long idxUser);

    @Transactional
    @Modifying
    @Query("delete from CardIssuanceInfo  where idxuser = :idxUser and disabled = false")
    void deleteAllByUserIdx(@Param("idxUser") Long idxUser);

    int countByCardCompanyAndIssuanceStatus(CardCompany cardCompany, IssuanceStatus issuanceStatus);

    @Query(value ="select ifnull(sum(grantLimit),0) as cnt from CardIssuanceInfo where cardCompany = :cardCompany", nativeQuery = true)
    Long sumGrantLimit(@Param("cardCompany") String cardCompany);


    interface dashBoardMonthDto{
        String getYearMonth();
        Long getTotalGrantLimit();
        Integer getCorpCnt();
    }

    @Query(value ="SELECT DATE_FORMAT(updatedAt,'%Y%m') AS yearmonth , sum(grantLimit) AS totalGrantLimit , count(*) AS corpCnt " +
            "    FROM CardIssuanceInfo  " +
            "    WHERE updatedAt > DATE_ADD(NOW(),INTERVAL -5 MONTH) " +
            "          AND issuanceStatus = 'ISSUED' " +
            "    GROUP BY DATE_FORMAT(updatedAt,'%Y%m') " +
            "    ORDER BY updatedAt", nativeQuery = true)
    List<dashBoardMonthDto> findDashBoardMonth();

    interface dashBoardWeekDto{
        String getStart();
        String getEnd();
        String getYearWeek();
        String getYearMonth();
        String getWeek();
        Long getTotalGrantLimit();
        Integer getCorpCnt();
    }

    @Query(value ="SELECT DATE_FORMAT(DATE_SUB(updatedAt, INTERVAL (DAYOFWEEK(updatedAt)-1) DAY), '%Y-%m-%d') as start, " +
            "       DATE_FORMAT(DATE_SUB(updatedAt, INTERVAL (DAYOFWEEK(updatedAt)-7) DAY), '%Y-%m-%d') as end, " +
            "       DATE_FORMAT(updatedAt, '%Y%U') AS yearWeek, " +
            "       DATE_FORMAT(updatedAt,'%Y%m') AS yearMonth, " +
            "       IF( week(DATE_FORMAT(updatedAt,'%Y%m01')) = 0 , WEEK(updatedAt) , (WEEK(updatedAt) - WEEK(DATE_FORMAT(updatedAt,'%Y%m01')))+1) AS week, " +
            "       SUM(grantLimit) AS totalGrantLimit, " +
            "       COUNT(*) AS corpCnt " +
            "  FROM CardIssuanceInfo " +
            "  WHERE updatedAt > DATE_FORMAT(DATE_ADD(NOW(),INTERVAL -5 MONTH), '%Y%m01') " +
            "       AND issuanceStatus = 'ISSUED' " +
            " GROUP BY yearWeek ", nativeQuery = true)
    List<dashBoardWeekDto> findDashBoardWeek();
}
