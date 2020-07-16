package com.nomadconnection.dapp.core.domain.repository.res;

import com.nomadconnection.dapp.core.domain.repository.querydsl.ResAccountCustomRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResAccountRepository extends JpaRepository<ResAccount, Long>, ResAccountCustomRepository {

    List<ResAccount> findByConnectedIdAndResAccountDepositIn(String connectedId, List<String> resAccountDeposit);

    @Query(value = " select R " +
            " from ResAccount R" +
            " where connectedId in (select connectedId from ConnectedMng where idxUser = :idxUser ) " +
            " order by field(resAccountDeposit , 10,11,12,13,14,30,20,40), resAccountNickName ASC, resAccountName ASC ")
    List<ResAccount> findConnectedId(Long idxUser);

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
            " R.resAccountCurrency      " +
            " from ResAccountHistory A      " +
            " join ResAccount R on R.ResAccount = A.ResAccount       " +
            " and connectedId in (select connectedId from ConnectedMng where idxUser = :idxUser)     " +
            " where (A.resAccountTrDate between :startDate and :endDate or :endDate is null)      " +
            " and (R.resAccount = :resAccount or :resAccount is null)      " +
            " and R.resAccountDeposit != if( :boolF = 1 , '00' , '20')  " +
            " and cast(resAccountIn as signed) >= :resAccountIn and cast(resAccountOut as signed) >= :resAccountOut " +
            " order by resAccountTrDate desc, resAccountTrTime desc, A.idx  " +
            " LIMIT :limit OFFSET :offset  ", nativeQuery = true)
    List<CaccountHistoryDto> findAccountHistory(String startDate, String endDate, String resAccount, Long idxUser, Integer limit, Integer offset, Integer resAccountIn, Integer resAccountOut, Integer boolF);

    public static interface CaccountCountDto {
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
    List<CaccountCountDto> findDayHistory(String startDate, String endDate, Long idxUser);

    public static interface CaccountMonthDto {
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
            "           (select if(ms >= left(resAccountStartDate,6) ,resAccountBalance,0 ) resAccountBalance      " +
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
    List<CaccountMonthDto> findMonthHistory(String startMonth, String endMonth, Long idxUser);

    @Query(value = "select ms sumDate, COALESCE(sumResAccountIn,0) sumResAccountIn, COALESCE(sumResAccountOut,0) sumResAccountOut, lastResAfterTranBalance     \n" +
            " from (     \n" +
            "     select ms, sum(     \n" +
            "   ifnull(     \n" +
            "    ifnull(     \n" +
            "     (select resAfterTranBalance from ResAccountHistory r      \n" +
            "      where Date_Format(resAccountTrDate,  '%Y%m') <= ms and b.resAccount = r.resAccount      \n" +
            "      order by Date_Format(resAccountTrDate,  '%Y%m') desc , resAccountTrDate desc, resAccountTrTime desc, idx limit 1)     \n" +
            "      ,     \n" +
            "           (select resAfterTranBalance - ABS(resAccountIn) + ABS(resAccountOut) from ResAccountHistory r      \n" +
            "       where Date_Format(resAccountTrDate,  '%Y%m') >= ms and b.resAccount = r.resAccount      \n" +
            "       order by Date_Format(resAccountTrDate,  '%Y%m') asc , resAccountTrDate asc, resAccountTrTime asc, idx asc limit 1)     \n" +
            "     ),     \n" +
            "           (select if(ms >= left(resAccountStartDate,6) ,resAccountBalance,0 ) resAccountBalance      \n" +
            "      from ResAccount r where b.resAccount = r.resAccount limit 1 )      \n" +
            "           )     \n" +
            "   ) lastResAfterTranBalance     \n" +
            "  from (select ms from date_t c where ds >= :startMonth and ds <= :endMonth group by ms) groupA     \n" +
            "    join ResAccount b on b.connectedId in (select connectedId from  ConnectedMng c where c.idxUser = :idxUser order by resAccount desc ) and resAccountDeposit in ('10','11','12','13','14','30')    \n" +
            "  group by ms     \n" +
            " ) groupTableA     \n" +
            "  left join (\n" +
            "        select      \n" +
            "  Date_Format(resAccountTrDate,  '%Y%m') resAccountTrMonth ,      \n" +
            "  sum(resAccountIn) sumResAccountIn ,     \n" +
            "  sum(resAccountOut) sumResAccountOut      \n" +
            "  from ResAccountHistory a1                       \n" +
            "   join ResAccount b on b.resAccount = a1.resAccount and resAccountDeposit in ('10','11','12','13','14')     \n" +
            "   join ConnectedMng c  on c.connectedId = b.connectedId and c.idxUser = :idxUser     \n" +
            "  where Date_Format(resAccountTrDate,  '%Y%m%d') >= :startMonth and  Date_Format(resAccountTrDate,  '%Y%m%d') <= :endMonth     \n" +
            "  group by Date_Format(resAccountTrDate,  '%Y%m')     \n" +
            " ) groupTableB  on groupTableA.ms = groupTableB.resAccountTrMonth order by sumDate", nativeQuery = true)
    List<CaccountMonthDto> findMonthHistory_External(String startMonth, String endMonth, Long idxUser);

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
            "       (select if(ms >= left(resAccountStartDate,6),resAccountBalance,0 ) resAccountBalance   " +
            "      from ResAccount r where b.resAccount = r.resAccount  limit 1 )   " +
            "                )  " +
            "   ) lastResAfterTranBalance  " +
            "  from (select ms from date_t c where ms between Date_Format( date_add(now(), INTERVAL - 4 month),  '%Y%m') and Date_Format( date_add(now(), INTERVAL - 1 month),  '%Y%m') group by ms) groupA   " +
            "    join ResAccount b on b.connectedId in (select connectedId from  ConnectedMng c where c.idxUser = :idxUser order by resAccount desc ) " +
            "       and resAccountDeposit in ('10','11','12','13','14')  " +
            "  group by ms", nativeQuery = true)
    List<Long> findBalance(Long idxUser);

    Optional<ResAccount> findByConnectedIdAndResAccount(String connectedId, String resAccount);

    Optional<ResAccount> findByResAccount(String resAccount);

    public static interface CRisk {
        Integer getDsc();

        String getDs();

        float getCurrentBalance();

        String getErrCnt();

        String getErrCode();
    }

    @Query(value = "select DATEDIFF(:calcDate,ds) dsc " +
            " , ds" +
            " , sum(ifnull(ifnull(if(errCnt is null, value1,0),if(errCnt is null, value2,0)),if(errCnt is null, value3,0))) as currentBalance \n" +
            " , max(errCnt) errCnt " +
            " , max(errCode) errCode " +
            "from  \n" +
            "(  select ds, resAccount, errCnt, errCode \n" +
            "  , (select resAfterTranBalance from ResAccountHistory r\n" +
            "     where resAccountTrDate <= ds and b.resAccount = r.resAccount \n" +
            "     order by resAccountTrDate desc , resAccountTrDate desc, resAccountTrTime desc, idx limit 1) value1 \n" +
            "  , (select resAfterTranBalance - ABS(resAccountIn) + ABS(resAccountOut) from ResAccountHistory r       \n" +
            "    where resAccountTrDate >= ds and b.resAccount = r.resAccount\n" +
            "    order by resAccountTrDate asc , resAccountTrDate asc, resAccountTrTime asc, idx asc limit 1) value2   \n" +
            "  , (select if( ds >= resAccountStartDate ,resAccountBalance,0 ) resAccountBalance       \n" +
            "  from ResAccount r where b.resAccount = r.resAccount limit 1 ) value3 \n" +
            "    from (select ds from date_t c where d between date_add(:calcDate, INTERVAL - 44 day) and :calcDate) g     \n" +
            "    join ResAccount b on b.connectedId in (select connectedId from  ConnectedMng c where c.idxUser = :idxUser  ) and resAccountDeposit in ('10','11','12','13','14') \n" +
            "    left join (select count(account) as errCnt, max(errCode) as errCode, account from ResBatchList r where errCode != 'CF-00000' and resBatchType = 1    \n" +
            "      and idxResBatch = (SELECT idxResBatch FROM ResBatchList where idxUser = :idxUser order by idxResBatch desc limit 1) group by account, errCode) c on c.account = b.resAccount\n" +
            ") a group by dsc order by dsc asc ", nativeQuery = true)
    List<CRisk> find45dayValance(Long idxUser, String calcDate);

    @Query(value = " select sum(ifnull(ifnull(if(errCnt is null, value1,0),if(errCnt is null, value2,0)),if(errCnt is null, value3,0))) as currentBalance \n" +
            "from  \n" +
            "( select resAccount, errCnt \n" +
            "  , (select resAfterTranBalance from ResAccountHistory r\n" +
            "     where resAccountTrDate <= :setDate  and b.resAccount = r.resAccount \n" +
            "     order by resAccountTrDate desc , resAccountTrDate desc, resAccountTrTime desc, idx limit 1) value1 \n" +
            "  , (select resAfterTranBalance - ABS(resAccountIn) + ABS(resAccountOut) from ResAccountHistory r       \n" +
            "    where resAccountTrDate >= :setDate and b.resAccount = r.resAccount\n" +
            "    order by resAccountTrDate asc , resAccountTrDate asc, resAccountTrTime asc, idx asc limit 1) value2   \n" +
            "  , (select if( :setDate >= resAccountStartDate ,resAccountBalance,0 ) resAccountBalance       \n" +
            "  from ResAccount r where b.resAccount = r.resAccount limit 1 ) value3 \n" +
            "from ResAccount b     \n" +
            "\tleft join (select count(account) as errCnt, account from ResBatchList r where errCode != 'CF-00000' and resBatchType = 1  \n" +
            "      and idxResBatch = (SELECT idxResBatch FROM ResBatchList where idxUser = :idxUser order by idxResBatch desc limit 1) group by account) c on c.account = b.resAccount\n" +
            "where b.connectedId in (select connectedId from  ConnectedMng c where c.idxUser = :idxUser  ) and resAccountDeposit in ('10','11','12','13','14') \n" +
            ") z ", nativeQuery = true)
    Double findRecentBalance(Long idxUser, String setDate);

    @Query(value = "select sum(resAccountRiskBalance) from ResAccount " +
            " where connectedId in (select connectedId from ConnectedMng where idxUser in (select idxUser from Corp where idx = :idxCorp))"
            , nativeQuery = true)
    Double findNowBalance(Long idxCorp);

    public static interface CaccountHistoryDto {
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
    Page<ResAccount> findExternalAccount(Pageable pageable, Long idxUser);


    public static interface CashResultDto {
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
            value = "select * from\n" +
                    "(select idxUser, idxCorp, resCompanyNm, resAccountIn, resAccountOut, (resAccountIn - resAccountOut) as  resAccountInOut ,0 as burnRate, 0 as runWay,  befoBalance, createdAt, errCode, if(errCode is null ,0,1) errStatus  from \n" +
                    "(select distinct u.idx as idxUser, u.idxCorp,   c.resCompanyNm, \n" +
                    "ifnull((select sum(resAccountIn) from ResAccountHistory  \n" +
                    "\twhere substr(resAccountTrDate, 1,6) = date_Format(now(),  '%Y%m') and resAccount in \n" +
                    "\t(select resAccount from ResAccount d WHERE d.connectedId in (select connectedId from ConnectedMng where idxUser = u.idx ) and  resAccountDeposit in ('10','11','12','13','14'))),0) as resAccountIn, \n" +
                    " ifnull((select sum(resAccountOut) from ResAccountHistory  where substr(resAccountTrDate, 1,6)  =  Date_Format(now(),  '%Y%m') \n" +
                    "\tand resAccount in  (select resAccount from ResAccount d WHERE d.connectedId in (select connectedId from ConnectedMng where idxUser = u.idx ) and  resAccountDeposit in ('10','11','12','13','14'))),0) as resAccountOut\n" +
                    ", ifnull((select currentBalance from Risk r\n" +
                    " where r.idxUser = u.idx and r.date = Date_Format(date_add(now(), INTERVAL - 1 DAY),  '%Y%m%d') ) , 0) befoBalance,       \n" +
                    " (select max(createdAt) from ResBatch where idxUser = u.idx) as createdAt,        \n" +
                    " (select max(errCode) from ResBatchList where errCode != 'CF-00000' and idxUser = u.idx and resBatchType = 1       \n" +
                    " and idxResBatch = (select max(idx) from ResBatch where idxUser = u.idx )) as errCode \n" +
                    " from User u inner join Corp c on c.idx = u.idxCorp \n" +
                    " join ConnectedMng cm  on cm.idxUser = u.idx ) z  \n" +
                    ") d where (resCompanyNm like concat('%',:searchCorpName,'%') or :searchCorpName is null ) and ( errStatus = :updateStatus or :updateStatus is null) ",
            countQuery = "select count(*) from\n" +
                    "(select idxUser, idxCorp, resCompanyNm, resAccountIn, resAccountOut, (resAccountIn - resAccountOut) as  resAccountInOut ,0 as burnRate, 0 as runWay,  befoBalance, createdAt, errCode, if(errCode is null ,0,1) errStatus  from \n" +
                    "(select distinct u.idx as idxUser, u.idxCorp,   c.resCompanyNm, \n" +
                    "ifnull((select sum(resAccountIn) from ResAccountHistory  \n" +
                    "\twhere substr(resAccountTrDate, 1,6) = date_Format(now(),  '%Y%m') and resAccount in \n" +
                    "\t(select resAccount from ResAccount d WHERE d.connectedId in (select connectedId from ConnectedMng where idxUser = u.idx ) and  resAccountDeposit in ('10','11','12','13','14'))),0) as resAccountIn, \n" +
                    " ifnull((select sum(resAccountOut) from ResAccountHistory  where substr(resAccountTrDate, 1,6)  =  Date_Format(now(),  '%Y%m') \n" +
                    "\tand resAccount in  (select resAccount from ResAccount d WHERE d.connectedId in (select connectedId from ConnectedMng where idxUser = u.idx ) and  resAccountDeposit in ('10','11','12','13','14'))),0) as resAccountOut\n" +
                    ", ifnull((select currentBalance from Risk r\n" +
                    " where r.idxUser = u.idx and r.date = Date_Format(date_add(now(), INTERVAL - 1 DAY),  '%Y%m%d') ) , 0) befoBalance,       \n" +
                    " (select max(createdAt) from ResBatch where idxUser = u.idx) as createdAt,        \n" +
                    " (select max(errCode) from ResBatchList where errCode != 'CF-00000' and idxUser = u.idx and resBatchType = 1       \n" +
                    " and idxResBatch = (select max(idx) from ResBatch where idxUser = u.idx )) as errCode \n" +
                    " from User u inner join Corp c on c.idx = u.idxCorp \n" +
                    " join ConnectedMng cm  on cm.idxUser = u.idx ) z  \n" +
                    ") d where (resCompanyNm like concat('%',:searchCorpName,'%') or :searchCorpName is null ) and ( errStatus = :updateStatus or :updateStatus is null) ",
            nativeQuery = true
    )
    Page<CashResultDto> cashList(String searchCorpName, Boolean updateStatus, Pageable pageable);

}