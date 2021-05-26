package com.nomadconnection.dapp.core.domain.repository.res;

import com.nomadconnection.dapp.core.domain.repository.querydsl.ResAccountCustomRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResAccountRepository extends JpaRepository<ResAccount, Long>, ResAccountCustomRepository {

    @Query(value = " select R " +
            " from ResAccount R" +
            " where connectedId in (select connectedId from ConnectedMng where idxUser = :idxUser ) " +
            " order by field(resAccountDeposit , 10,11,12,13,14,30,20,40), resAccountNickName ASC, resAccountName ASC ")
    List<ResAccount> findResAccount(@Param("idxUser") Long idxUser);

    @Query(value = " select R " +
            " from ResAccount R" +
            " where connectedId in (select connectedId from ConnectedMng where idxUser = :idxUser and status = 'NORMAL') " +
            " order by field(resAccountDeposit , 10,11,12,13,14,30,20,40), resAccountNickName ASC, resAccountName ASC ")
    List<ResAccount> findResAccountStatus(@Param("idxUser") Long idxUser);

    @Query(value = " select      " +
            " R.resAccountDeposit,     " +
            " R.resAccountDisplay AS resAccount,      " +
            " ifnull ( nullif(R.nickName, ''), ifnull ( nullif(R.resAccountNickName,''), nullif(R.resAccountName,'') )) accountName,      " +
            " R.resAccountBalance,     " +
            " A.resAccountTrDate,      " +
            " A.resAccountTrTime,      " +
            " if(A.resAccountOut > 0 , A.resAccountOut * -1, resAccountIn) resAccountInOut,       " +
            " A.resAccountDesc1,      " +
            " A.resAccountDesc2,      " +
            " A.resAccountDesc3,      " +
            " A.resAccountDesc4,      " +
            " A.resAfterTranBalance,      " +
            " A.resAccountCurrency      " +
            " from ResAccountHistory A      " +
            " join ResAccount R on R.ResAccount = A.ResAccount and A.resAccountCurrency = R.resAccountCurrency      " +
            " and connectedId in (select connectedId from ConnectedMng where idxUser = :idxUser)     " +
            " where (A.resAccountTrDate between :startDate and :endDate or :endDate is null)      " +
            " and A.resAccountCurrency = :resAccountCurrency " +
            " and (R.resAccount = :resAccount or :resAccount is null)      " +
            " and R.resAccountDeposit != if( :boolF = 1 , '00' , '20')  " +
            " and cast(resAccountIn as signed) >= :resAccountIn and cast(resAccountOut as signed) >= :resAccountOut " +
            " order by resAccountTrDate desc, resAccountTrTime desc, A.idx  " +
            " LIMIT :limit OFFSET :offset  ", nativeQuery = true)
    List<CaccountHistoryDto> findAccountHistory(@Param("startDate") String startDate, @Param("endDate") String endDate,
                                                @Param("resAccount") String resAccount, @Param("idxUser") Long idxUser,
                                                @Param("limit") Integer limit, @Param("offset") Integer offset,
                                                @Param("resAccountIn") Integer resAccountIn,
                                                @Param("resAccountOut") Integer resAccountOut, @Param("boolF") Integer boolF,
                                                @Param("resAccountCurrency") String resAccountCurrency);

    Optional<ResAccount> findByResAccountAndResAccountEndDate(String resAccount, String strResLastTranDate);

    Optional<ResAccount> findTopByConnectedIdAndResAccount(String connectedId, String resAccount);

    Optional<ResAccount> findByResAccountAndResAccountCurrency(String resAccount, String resAccountCurrency);

    @Transactional
    @Modifying
    @Query("delete from ResAccount where resAccount = :resAccount")
    void deleteByResAccount(@Param("resAccount") String resAccount);

    @Transactional
    @Modifying
    @Query("update ResAccount set status = 'ERROR' where status = 'NORMAL' " +
            " and connectedId in (select connectedId from ConnectedMng where idxCorp = :idxCorp ) ")
    int accountStatusError(@Param("idxCorp") Long idxCorp);

    @Transactional
    @Modifying
    @Query("update ResBatch set endFlag = true where idxUser = :idxUser and endFlag = false ")
    int endBatchUser(@Param("idxUser") Long idxUser);

    Optional<ResAccount> findTopByResAccountAndResAccountCurrency(String resAccount, String resAccountCurrency);

    interface CaccountCountDto {
        String getSumDate();

        Long getSumResAccountIn();

        Long getSumResAccountOut();
    }

    @Query(value = "select dm as sumDate, COALESCE(sumResAccountIn,0) sumResAccountIn,   " +
            "        COALESCE(sumResAccountOut,0) sumResAccountOut   " +
            "    from   " +
            "        (select c.ds as dm from date_t c where ds between  :startDate and  :endDate group by dm ) groupA        " +
            "    left join   " +
            "        (   " +
            "            select resAccountTrDate resAccountTrDate ,   " +
            "                sum(resAccountIn) sumResAccountIn ,   " +
            "                sum(resAccountOut) sumResAccountOut    " +
            "                from ResAccountHistory a                      " +
            "          join ResAccount b on b.resAccount = a.resAccount and resAccountDeposit in ('10','11','12','13','14')   " +
            "          join ConnectedMng c  on c.connectedId = b.connectedId and c.idxUser = :idxUser   " +
            "                where resAccountTrDate  between  :startDate and  :endDate     " +
            "                group by resAccountTrDate ) groupB    " +
            "                on groupA.dm = groupB.resAccountTrDate  where groupA.dm between :startDate and  :endDate   " +
            "            order by groupA.dm desc", nativeQuery = true)
    List<CaccountCountDto> findDayHistory(@Param("startDate") String startDate,
                                          @Param("endDate") String endDate,
                                          @Param("idxUser") Long idxUser);

    interface CaccountMonthDto {
        String getSumDate();

        Long getSumResAccountIn();

        Long getSumResAccountOut();

        Long getLastResAfterTranBalance();
    }

    @Query(value = "select ms sumDate, COALESCE(sumResAccountIn,0) sumResAccountIn, COALESCE(sumResAccountOut,0) sumResAccountOut, lastResAfterTranBalance     " +
            " from (     " +
            "     select ms, sum(     " +
            "   ifnull(     " +
            "    ifnull(     " +
            "     (select resAfterTranBalance from ResAccountHistory r      " +
            "      where Date_Format(resAccountTrDate,  '%Y%m') <= ms and b.resAccount = r.resAccount      " +
            "      order by Date_Format(resAccountTrDate,  '%Y%m') desc , resAccountTrDate desc, resAccountTrTime desc, idx limit 1)     " +
            "      ,     " +
            "           (select resAfterTranBalance - ABS(resAccountIn) + ABS(resAccountOut) from ResAccountHistory r      " +
            "       where Date_Format(resAccountTrDate,  '%Y%m') >= ms and b.resAccount = r.resAccount      " +
            "       order by Date_Format(resAccountTrDate,  '%Y%m') asc , resAccountTrDate asc, resAccountTrTime asc, idx asc limit 1)     " +
            "     ),     " +
            "           (select if(ms >= left(searchStartDate,6) ,resAccountBalance,0 ) resAccountBalance      " +
            "      from ResAccount r where b.resAccount = r.resAccount limit 1 )      " +
            "           )     " +
            "   ) lastResAfterTranBalance     " +
            "  from (select ms from date_t c where ms >= :startMonth and ms <= :endMonth group by ms) groupA     " +
            "    join ResAccount b on b.connectedId in (select connectedId from  ConnectedMng c where c.idxUser = :idxUser order by resAccount desc ) and resAccountDeposit in ('10','11','12','13','14','30')    " +
            "  group by ms     " +
            " ) groupTableA     " +
            "  left join (     " +
            "        select      " +
            "  Date_Format(resAccountTrDate,  '%Y%m') resAccountTrMonth ,      " +
            "  sum(resAccountIn) sumResAccountIn ,     " +
            "  sum(resAccountOut) sumResAccountOut      " +
            "  from ResAccountHistory a1                       " +
            "   join ResAccount b on b.resAccount = a1.resAccount and resAccountDeposit in ('10','11','12','13','14')     " +
            "   join ConnectedMng c  on c.connectedId = b.connectedId and c.idxUser = :idxUser     " +
            "  where Date_Format(resAccountTrDate,  '%Y%m') >= :startMonth and  Date_Format(resAccountTrDate,  '%Y%m') <= :endMonth     " +
            "  group by Date_Format(resAccountTrDate,  '%Y%m')     " +
            " ) groupTableB  on groupTableA.ms = groupTableB.resAccountTrMonth order by sumDate", nativeQuery = true)
    List<CaccountMonthDto> findMonthHistory(@Param("startMonth") String startMonth, @Param("endMonth")String endMonth, @Param("idxUser") Long idxUser);

    @Query(value = "select ms sumDate, COALESCE(sumResAccountIn,0) sumResAccountIn, COALESCE(sumResAccountOut,0) sumResAccountOut, lastResAfterTranBalance     " +
            " from (     " +
            "     select ms, sum(     " +
            "   ifnull(     " +
            "    ifnull(     " +
            "     (select resAfterTranBalance from ResAccountHistory r      " +
            "      where Date_Format(resAccountTrDate,  '%Y%m') <= ms and b.resAccount = r.resAccount      " +
            "      order by Date_Format(resAccountTrDate,  '%Y%m') desc , resAccountTrDate desc, resAccountTrTime desc, idx limit 1)     " +
            "      ,     " +
            "           (select resAfterTranBalance - ABS(resAccountIn) + ABS(resAccountOut) from ResAccountHistory r      " +
            "       where Date_Format(resAccountTrDate,  '%Y%m') >= ms and b.resAccount = r.resAccount      " +
            "       order by Date_Format(resAccountTrDate,  '%Y%m') asc , resAccountTrDate asc, resAccountTrTime asc, idx asc limit 1)     " +
            "     ),     " +
            "           (select if(ms >= left(searchStartDate,6) ,resAccountBalance,0 ) resAccountBalance      " +
            "      from ResAccount r where b.resAccount = r.resAccount limit 1 )      " +
            "           )     " +
            "   ) lastResAfterTranBalance     " +
            "  from (select ms from date_t c where ds >= :startMonth and ds <= :endMonth group by ms) groupA     " +
            "    join ResAccount b on b.connectedId in (select connectedId from  ConnectedMng c where c.idxUser = :idxUser order by resAccount desc ) and resAccountDeposit in ('10','11','12','13','14','30')    " +
            "  group by ms     " +
            " ) groupTableA     " +
            "  left join (" +
            "        select      " +
            "  Date_Format(resAccountTrDate,  '%Y%m') resAccountTrMonth ,      " +
            "  sum(resAccountIn) sumResAccountIn ,     " +
            "  sum(resAccountOut) sumResAccountOut      " +
            "  from ResAccountHistory a1                       " +
            "   join ResAccount b on b.resAccount = a1.resAccount and resAccountDeposit in ('10','11','12','13','14')     " +
            "   join ConnectedMng c  on c.connectedId = b.connectedId and c.idxUser = :idxUser     " +
            "  where Date_Format(resAccountTrDate,  '%Y%m%d') >= :startMonth and  Date_Format(resAccountTrDate,  '%Y%m%d') <= :endMonth     " +
            "  group by Date_Format(resAccountTrDate,  '%Y%m')     " +
            " ) groupTableB  on groupTableA.ms = groupTableB.resAccountTrMonth order by sumDate", nativeQuery = true)
    List<CaccountMonthDto> findMonthHistory_External(@Param("startMonth") String startMonth,
                                                     @Param("endMonth") String endMonth,
                                                     @Param("idxUser") Long idxUser);

    @Query(value = "select  " +
            "  sum(  " +
            "   ifnull(  " +
            "    ifnull(  " +
            "     (select resAfterTranBalance from ResAccountHistory r   " +
            "      where Date_Format(resAccountTrDate,  '%Y%m') <= ms and b.resAccount = r.resAccount   " +
            "      order by Date_Format(resAccountTrDate,  '%Y%m') desc , resAccountTrDate desc, resAccountTrTime desc, idx limit 1)  " +
            "      ,  " +
            "       (select resAfterTranBalance - ABS(resAccountIn) + ABS(resAccountOut) from ResAccountHistory r   " +
            "       where Date_Format(resAccountTrDate,  '%Y%m') >= ms and b.resAccount = r.resAccount   " +
            "       order by Date_Format(resAccountTrDate,  '%Y%m') asc , resAccountTrDate asc, resAccountTrTime asc, idx asc limit 1)  " +
            "     ),  " +
            "       (select if(ms >= left(searchStartDate,6),resAccountBalance,0 ) resAccountBalance   " +
            "      from ResAccount r where b.resAccount = r.resAccount  limit 1 )   " +
            "                )  " +
            "   ) lastResAfterTranBalance  " +
            "  from (select ms from date_t c where ms between Date_Format( date_add(now(), INTERVAL - 4 month),  '%Y%m') and Date_Format( date_add(now(), INTERVAL - 1 month),  '%Y%m') group by ms) groupA   " +
            "    join ResAccount b on b.connectedId in (select connectedId from  ConnectedMng c where c.idxUser = :idxUser order by resAccount desc ) " +
            "       and resAccountDeposit in ('10','11','12','13','14')  " +
            "  group by ms", nativeQuery = true)
    List<Long> findBalance(@Param("idxUser") Long idxUser);

    Optional<ResAccount> findByConnectedIdAndResAccount(String connectedId, String resAccount);

    Optional<ResAccount> findTopByResAccount(String resAccount);

    @Query(value =
            "SELECT IFNULL(SUM(IFNULL(IFNULL(IFNULL(value1, value2), value3), 0)), 0) AS currentBalancer " +
                " FROM (SELECT resAccount" +
                "           , (SELECT resAfterTranBalance" +
                "              FROM ResAccountHistory r" +
                "              WHERE resAccountTrDate <= :setDate" +
                "                AND b.resAccount = r.resAccount" +
                "              ORDER BY resAccountTrDate DESC, resAccountTrTime DESC, idx" +
                "              LIMIT 1) value1" +
                "           , (SELECT resAfterTranBalance - ABS(resAccountIn) + ABS(resAccountOut)" +
                "              FROM ResAccountHistory r" +
                "              WHERE resAccountTrDate > :setDate" +
                "                AND b.resAccount = r.resAccount" +
                "              ORDER BY resAccountTrDate ASC, resAccountTrTime ASC, idx ASC" +
                "              LIMIT 1) value2" +
                "           , (SELECT IF(:setDate >= searchStartDate, resAccountBalance, 0) resAccountBalance" +
                "              FROM ResAccount r" +
                "              WHERE b.resAccount = r.resAccount" +
                "              LIMIT 1) value3" +
                "      FROM ResAccount b" +
                "               INNER JOIN (" +
                "          SELECT account," +
                "                 endDate," +
                "                 updatedAt," +
                "                 errCode," +
                "                 errMessage," +
                "                 CASE" +
                "                     WHEN @vaccount = account" +
                "                         THEN @id\\:=@id + 1" +
                "                     ELSE @id\\:= 1" +
                "                     END AS RANK, @vaccount\\:= account AS dummy" +
                "          FROM ResBatchList, (SELECT @vaccount \\:= NULL, @id \\:= 0) AS t" +
                "          WHERE idxUser = :idxUser" +
                "            AND endDate >= :setDate" +
                "            AND resBatchType = 1" +
                "            AND updatedAt" +
                "              > date_add(now()" +
                "              , INTERVAL -1 DAY)" +
                "          ORDER BY ACCOUNT, endDate DESC, updatedAt DESC) c" +
                "                          ON RANK = 1 AND errCode = 'CF-00000' AND c.account = b.resAccount" +
                "      WHERE b.connectedId IN (SELECT connectedId FROM ConnectedMng c WHERE c.idxUser = :idxUser)" +
                "        AND resAccountDeposit IN ('10', '11', '12', '13', '14')) z", nativeQuery = true)
    Double findRecentBalance(@Param("idxUser") Long idxUser, @Param("setDate") String setDate);


    @Query(value =" select ifnull(sum(if(r.resAccountCurrency = 'KRW', r.resAccountBalance, r.resAccountBalance * a.receiving)),0) as balance " +
            "from  ResAccount r " +
            "    inner join (select receiving, country, max(date)  from ResExchangeRate group by country) a on a.country = r.resAccountCurrency " +
            "    inner join ResConCorpList rccl on rccl.connectedId = r.connectedId and rccl.status in ('NORMAL','ERROR') and r.organization = rccl.organization and rccl.businessType = 'BK' " +
            "    inner join ConnectedMng cm on cm.connectedId = rccl.connectedId and cm.status in ('NORMAL','ERROR') and cm.idxCorp = :idxCorp " +
            " where (r.status in ('NORMAL') AND r.resAccountDeposit IN ('10', '11', '12', '14', '20') and (r.resOverdraftAcctYN is null or r.resOverdraftAcctYN = 0 )) " +
            " or (r.status in ('NORMAL') AND r.resAccountDeposit = '13' and r.resAccountName NOT LIKE '%퇴직%' and (r.resOverdraftAcctYN is null or r.resOverdraftAcctYN = 0 ))"
            , nativeQuery = true)
    Double findRecentBalanceToDay(@Param("idxCorp") Long idxCorp);

    interface CaccountHistoryDto {
        String getResAccount();

        String getAccountName();

        String getResAccountBalance();

        String getResAccountTrDate();

        String getResAccountTrTime();

        String getResAccountInOut();

        String getResAccountDesc1();

        String getResAccountDesc2();

        String getResAccountDesc3();

        String getResAccountDesc4();

        String getResAfterTranBalance();

        String getResAccountDeposit();

        String getResAccountCurrency();
    }

    @Query(
            value = "select R.* from ResAccount R where connectedId in (select connectedId from ConnectedMng where idxUser =:idxUser)",
            countQuery = "select count(*) from ResAccount R where connectedId in (select connectedId from ConnectedMng where idxUser =:idxUser) ",
            nativeQuery = true
    )
    Page<ResAccount> findExternalAccount(@Param("pageable") Pageable pageable, @Param("idxUser") Long idxUser);


    interface CashResultDto {
        Long getIdxUser();

        Long getIdxCorp();

        String getResCompanyNm();

        Double getResAccountIn();

        Double getResAccountOut();

        Double getResAccountInOut();

        Long getBurnRate();

        Integer getRunWay();

        Double getBefoBalance();

        LocalDateTime getCreatedAt();

        String getErrCode();

        String getErrStatus();
    }

    @Query(
            value = "select * from" +
                    "(select idxUser, idxCorp, resCompanyNm, resAccountIn, resAccountOut, (resAccountIn - resAccountOut) as  resAccountInOut ,0 as burnRate, 0 as runWay,  befoBalance, createdAt, errCode, if(errCode is null ,0,1) errStatus  from " +
                    "(select distinct u.idx as idxUser, u.idxCorp,   c.resCompanyNm, " +
                    "ifnull((select sum(resAccountIn) from ResAccountHistory  " +
                    "where substr(resAccountTrDate, 1,6) = date_Format(now(),  '%Y%m') and resAccount in " +
                    "(select resAccount from ResAccount d WHERE d.connectedId in (select connectedId from ConnectedMng where idxUser = u.idx ) and  resAccountDeposit in ('10','11','12','13','14'))),0) as resAccountIn, " +
                    " ifnull((select sum(resAccountOut) from ResAccountHistory  where substr(resAccountTrDate, 1,6)  =  Date_Format(now(),  '%Y%m') " +
                    "and resAccount in  (select resAccount from ResAccount d WHERE d.connectedId in (select connectedId from ConnectedMng where idxUser = u.idx ) and  resAccountDeposit in ('10','11','12','13','14'))),0) as resAccountOut" +
                    ", ifnull((select currentBalance from Risk r" +
                    " where r.idxUser = u.idx and r.date = Date_Format(date_add(now(), INTERVAL - 1 DAY),  '%Y%m%d') ) , 0) befoBalance,       " +
                    " (select max(createdAt) from ResBatch where idxUser = u.idx) as createdAt,        " +
                    " (select max(errCode) from ResBatchList where errCode != 'CF-00000' and idxUser = u.idx and resBatchType = 1       " +
                    " and idxResBatch = (select max(idx) from ResBatch where idxUser = u.idx )) as errCode " +
                    " from User u inner join Corp c on c.idx = u.idxCorp " +
                    " join ConnectedMng cm  on cm.idxUser = u.idx ) z  " +
                    ") d where (resCompanyNm like concat('%',:searchCorpName,'%') or :searchCorpName is null ) and ( errStatus = :updateStatus or :updateStatus is null) ",
            countQuery = "select count(*) from" +
                    "(select idxUser, idxCorp, resCompanyNm, resAccountIn, resAccountOut, (resAccountIn - resAccountOut) as  resAccountInOut ,0 as burnRate, 0 as runWay,  befoBalance, createdAt, errCode, if(errCode is null ,0,1) errStatus  from " +
                    "(select distinct u.idx as idxUser, u.idxCorp,   c.resCompanyNm, " +
                    "ifnull((select sum(resAccountIn) from ResAccountHistory  " +
                    "where substr(resAccountTrDate, 1,6) = date_Format(now(),  '%Y%m') and resAccount in " +
                    "(select resAccount from ResAccount d WHERE d.connectedId in (select connectedId from ConnectedMng where idxUser = u.idx ) and  resAccountDeposit in ('10','11','12','13','14'))),0) as resAccountIn, " +
                    " ifnull((select sum(resAccountOut) from ResAccountHistory  where substr(resAccountTrDate, 1,6)  =  Date_Format(now(),  '%Y%m') " +
                    "and resAccount in  (select resAccount from ResAccount d WHERE d.connectedId in (select connectedId from ConnectedMng where idxUser = u.idx ) and  resAccountDeposit in ('10','11','12','13','14'))),0) as resAccountOut" +
                    ", ifnull((select currentBalance from Risk r" +
                    " where r.idxUser = u.idx and r.date = Date_Format(date_add(now(), INTERVAL - 1 DAY),  '%Y%m%d') ) , 0) befoBalance,       " +
                    " (select max(createdAt) from ResBatch where idxUser = u.idx) as createdAt,        " +
                    " (select max(errCode) from ResBatchList where errCode != 'CF-00000' and idxUser = u.idx and resBatchType = 1       " +
                    " and idxResBatch = (select max(idx) from ResBatch where idxUser = u.idx )) as errCode " +
                    " from User u inner join Corp c on c.idx = u.idxCorp " +
                    " join ConnectedMng cm  on cm.idxUser = u.idx ) z  " +
                    ") d where (resCompanyNm like concat('%',:searchCorpName,'%') or :searchCorpName is null ) and ( errStatus = :updateStatus or :updateStatus is null) ",
            nativeQuery = true
    )
    Page<CashResultDto> cashList(@Param("searchCorpName") String searchCorpName,
                                 @Param("updateStatus") Boolean updateStatus,
                                 @Param("pageable") Pageable pageable);

    List<ResAccount> findByConnectedIdInAndUpdatedAtAfter(List<String> connectedId, LocalDateTime localDateTime);




    @Query(value = "SELECT count(*) FROM ConnectedMng cm " +
            "JOIN ResAccount r ON r.connectedId = cm.connectedId AND resAccount IN (:arrayResAccount)" +
            "WHERE cm.idxCorp = :idxCorp " ,nativeQuery = true)
    Integer searchValidateResAccount(@Param("arrayResAccount")List<String> arrayResAccount,
                                     @Param("idxCorp")Long idxCorp);

    @Query(value = "SELECT count(*) FROM ConnectedMng cm " +
            "JOIN ResAccount r ON r.connectedId = cm.connectedId AND resAccount = :strResAccount " +
            "WHERE cm.idxCorp = :idxCorp " ,nativeQuery = true)
    Integer searchValidateResAccount(@Param("strResAccount") String resAccount,
                                     @Param("idxCorp")Long idxCorp);

    @Query(value =
            "SELECT IFNULL(SUM(IFNULL(IFNULL(IFNULL(value1, value2), value3), 0)), 0) AS currentBalancer " +
                    " FROM (SELECT resAccount" +
                    "           , (SELECT resAfterTranBalance" +
                    "              FROM ResAccountHistory r" +
                    "              WHERE resAccountTrDate <= :setDate" +
                    "                AND b.resAccount = r.resAccount" +
                    "              ORDER BY resAccountTrDate DESC, resAccountTrTime DESC, idx" +
                    "              LIMIT 1) value1" +
                    "           , (SELECT resAfterTranBalance - ABS(resAccountIn) + ABS(resAccountOut)" +
                    "              FROM ResAccountHistory r" +
                    "              WHERE resAccountTrDate > :setDate" +
                    "                AND b.resAccount = r.resAccount" +
                    "              ORDER BY resAccountTrDate ASC, resAccountTrTime ASC, idx ASC" +
                    "              LIMIT 1) value2" +
                    "           , (SELECT IF(:setDate >= searchStartDate, resAccountBalance, 0) resAccountBalance" +
                    "              FROM ResAccount r" +
                    "              WHERE b.resAccount = r.resAccount" +
                    "              LIMIT 1) value3" +
                    "      FROM ResAccount b" +
                    "               INNER JOIN (" +
                    "          SELECT account," +
                    "                 endDate," +
                    "                 updatedAt," +
                    "                 errCode," +
                    "                 errMessage," +
                    "                 CASE" +
                    "                     WHEN @vaccount = account" +
                    "                         THEN @id\\:=@id + 1" +
                    "                     ELSE @id\\:= 1" +
                    "                     END AS RANK, @vaccount\\:= account AS dummy" +
                    "          FROM ResBatchList, (SELECT @vaccount \\:= NULL, @id \\:= 0) AS t" +
                    "          WHERE idxCorp = :idxCorp" +
                    "            AND endDate >= :setDate" +
                    "            AND resBatchType = 1" +
                    "            AND updatedAt" +
                    "              > date_add(now()" +
                    "              , INTERVAL -1 DAY)" +
                    "          ORDER BY ACCOUNT, endDate DESC, updatedAt DESC) c" +
                    "                          ON RANK = 1 AND c.account = b.resAccount" +
                    "      WHERE b.connectedId IN (SELECT connectedId FROM ConnectedMng c WHERE c.idxCorp = :idxCorp)" +
                    "        AND resAccountDeposit IN ('10', '11', '12', '13', '14')) z", nativeQuery = true)
    Double recentBalanceCorp(@Param("idxCorp") Long idxCorp, @Param("setDate") String setDate);
}