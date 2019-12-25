package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.CardTransaction;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CardTransactionCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardTransactionRepository extends JpaRepository<CardTransaction, Long>, CardTransactionCustomRepository {

    // Corp 의 카드리스트
    @Query(value = "select distinct idxCard from User where idxCorp = :idxCorp and idxCard is not null", nativeQuery = true)
    List<Long> findCardList(@Param("idxCorp") Long idxCorp);


    // 카드리스트의 월간 총금액
    @Query(value = "select sum(usedAmount) as usedAmount from CardTransaction \n" +
            "where ( usedAt > LAST_DAY(STR_TO_DATE( CONCAT(:strYear , :strMonth , '01'), '%Y%m%d'))- interval 1 month) \n" +
            "AND usedAt <= LAST_DAY(STR_TO_DATE(CONCAT(:strYear , :strMonth , '01'), '%Y%m%d')) \n" +
            "AND idxCard in (:cards) "
            , nativeQuery = true)
    Long findMonthAmount(@Param("strYear") String strYear,@Param("strMonth") String strMonth, @Param("cards") List<Long> cards);

    // 카드정보로 카드이용 내역 출력 - 날짜별 총금액
    @Query(value = "select DATE_FORMAT(usedAt , '%m.%d' ) as asUsedAt \n" +
            ",SUBSTR( _UTF8'일월화수목금토' , DAYOFWEEK(usedAt), 1) AS week \n" +
            // ",CONCAT(sum(usedAmount),'')  as usedAmount  \n" +
            "from CardTransaction \n" +
            "where ( usedAt > LAST_DAY(STR_TO_DATE( CONCAT(:strDate), '%Y%m%d'))- interval 1 month) \n" +
            "AND usedAt <= LAST_DAY(STR_TO_DATE(CONCAT(:strDate), '%Y%m%d')) \n" +
            "AND idxCard in (:cards)" +
            "group by asUsedAt, week "
            , nativeQuery = true)
    List<PerDailyDto> findHistoryByDate(@Param("strDate") String strDate, @Param("cards") List<Long> cards);

    // 카드정보로 카드이용 내역 출력 - 항목별 총금액

    // 카드정보로 카드이용 내역 출력 - 지역별 총금액

    // 월별 카드사용금액 - 관리자
//    @Query(value = "select\n" +
//            "        c.idx,\n" +
//            "        c.cardNo,\n" +
//            "        c.idxUser,\n" +
//            "        sum(ct.usedAmount) as usedAmount  \n" +
//            "    from\n" +
//            "        CardTransaction ct\n" +
//            "        right join Card c on c.idxCorp = :idx and c.idx = ct.idxCard\n" +
//            "    where\n" +
//            "        (usedAt > LAST_DAY(STR_TO_DATE( CONCAT(:strDate), '%Y%m%d'))- interval 1 month)  \n" +
//            "        AND usedAt <= LAST_DAY(STR_TO_DATE(CONCAT(:strDate), '%Y%m%d'))   \n" +
//            "    group by\n" +
//            "        c.idx", nativeQuery = true)
//    List findCardAdmin(@Param("strDate") Integer strDate, Long idx);

//    // 월별 카드사용금액 - 사용자
//    @Query(value = "select\n" +
//            "        c.idx,\n" +
//            "        c.cardNo,\n" +
//            "        c.idxUser,\n" +
//            "        sum(ct.usedAmount) as usedAmount  \n" +
//            "    from\n" +
//            "        CardTransaction ct\n" +
//            "        right join Card c on c.idxUser = :idx and c.idx = ct.idxCard \n" +
//            "    where\n" +
//            "        (usedAt > LAST_DAY(STR_TO_DATE( CONCAT(:strDate), '%Y%m%d'))- interval 1 month)  \n" +
//            "        AND usedAt <= LAST_DAY(STR_TO_DATE(CONCAT(:strDate), '%Y%m%d'))   \n" +
//            "    group by\n" +
//            "        c.idx", nativeQuery = true)
//    List findCardUser(@Param("strDate") String strDate, Long idx);



    // 카드 상세 내역

}
