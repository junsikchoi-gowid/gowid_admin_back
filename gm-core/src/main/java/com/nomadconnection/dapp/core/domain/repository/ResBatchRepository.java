package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResBatch;
import com.nomadconnection.dapp.core.domain.ResBatchList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ResBatchRepository extends JpaRepository<ResBatch, Long> {

    @Query(value = "select \n" +
            "    a.idx as idx, \n" +
            "    a.min as min, \n" +
            "    a.endFlag as endFlag, \n" +
            "    ifnull(b.errCode ,'') as errCode, \n" +
            "    ifnull(b.errMessage ,'') as errMessage, \n" +
            "    ifnull(b.resBatchType ,'') as resBatchType, \n" +
            "    (select count(*) from ResAccount  \n" +
            "  where connectedId in (select connectedId from ConnectedMng where idxUser = :idxUser )) as total , \n" +
            "    (select count(distinct account) from ResBatchList where a.idx = idxResBatch and resBatchType = 1) as progressCnt, \n" +
            "    (select count(distinct account) from ResBatchList where a.idx = idxResBatch and errCode <> 'CF-00000' and resBatchType = 1 ) as errorCnt \n" +
            "from \n" +
            "    (select  idx,  TIMESTAMPDIFF(MINUTE,   now() ,   r.updatedAt) * -1 As min,   endFlag   \n" +
            "    from ResBatch r     \n" +
            "    where \n" +
            "        idxUser = :idxUser\n" +
            "    order by \n" +
            "        updatedAt desc limit 1 ) a       \n" +
            "left join \n" +
            "    ResBatchList b   \n" +
            "        on a.idx = b.idxResBatch  \n" +
            "        and b.errCode <> 'CF-00000'  \n" +
            "        and resBatchType = 1",nativeQuery = true)
    List<ResBatchRepository.CResBatchDto> findRefresh(Long idxUser);

    @Query(value = "select comm.resAccountStartDate\n" +
            ", if(Date_Format(resAccountStartDate , '%Y%m') = Date_Format(startDay , '%Y%m'), resAccountStartDate, startDay) startDay\n" +
            ", comm.endDay as endDay\n" +
            ", comm.resAccount as resAccount\n" +
            ", comm.connectedId as connectedId\n" +
            ", comm.organization as  organization\n" +
            ", comm.ResAccountDeposit as ResAccountDeposit\n" +
            ", comm.resAccountCurrency as resAccountCurrency\n" +
            ", comm.nowMonth as nowMonth " +
            ", comm.errCode as errCode " +
            " from ( select \n" +
            "case  \n" +
            "when organization = 0003 then 201301\n" +
            "when organization = 0007 then date_format(date_add(now(), INTERVAL - 5 year), '%Y%m%d')\n" +
            "when organization = 0020 then date_format(date_add(now(), INTERVAL - 12 month), '%Y%m%d')\n" +
            "when organization = 0027 then date_format(date_add(now(), INTERVAL - 6 month), '%Y%m%d')\n" +
            "when organization = 0048 then date_format(date_add(now(), INTERVAL - 5 year), '%Y%m%d')\n" +
            "when organization = 0081 then date_format(date_add(now(), INTERVAL - 12 month), '%Y%m%d')\n" +
            "else A.resAccountStartDate\n" +
            "end AS resAccountStartDate\n" +
            ", A.startDay\n" +
            ", A.endDay\n" +
            ", A.resAccount\n" +
            ", A.connectedId\n" +
            ", A.organization       \n" +
            ", A.resAccountDeposit \n" +
            ", A.resAccountCurrency \n" +
            ", A.nowMonth \n" +
            ", A.errCode \n" +
            "from (\n" +
            "select \n" +
            "(select errCode from ResBatchList where account = main.resAccount and Date_Format(startDate, '%Y%m') = Date_Format(main.startDay, '%Y%m') order by idx desc limit 1) errCode,\n" +
            "main.*, if(Date_Format(now() , '%Y%m') = Date_Format(main.startDay, '%Y%m'), 1, 0 ) as nowMonth from \n" +
            "(select if(y = Date_Format(resAccountStartDate , '%Y%m'), resAccountStartDate, concat(L.y,'01')) startDay\n" +
            ", if(y = Date_Format(now() , '%Y%m'), Date_Format(now() , '%Y%m%d'), date_format(last_day(concat(L.y,'01')),'%Y%m%d')) as endDay\n" +
            ", R.resAccountStartDate\n" +
            ", R.resAccount\n" +
            ", R.connectedId\n" +
            ", R.organization       \n" +
            ", R.resAccountDeposit \n" +
            ", R.resAccountCurrency \n" +
            "from (\n" +
            "select y from\n" +
            "(\n" +
            "SELECT date_format(  date_add(now(), INTERVAL - (a.a + (10 * b.a) + (100 * c.a)) month), '%Y%m') y\n" +
            "from (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as a\n" +
            "cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as b\n" +
            "cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as c\n" +
            ") a where a.y >  date_format(date_add(now(), INTERVAL - 1 year), '%Y%m')\n" +
            ") L\n" +
            "join ResAccount R \n" +
            "join ConnectedMng cm on R.connectedId = cm.connectedId and idxUser = :idxUser\n" +
            ") main\n" +
            ") A \n" +
            ") comm where ((errCode is null or errCode != 'CF-00000') and Date_Format(startDay , '%Y%m') >= Date_Format(resAccountStartDate , '%Y%m')) or nowMonth = 1\n" +
            "order by startDay desc, endDay desc",nativeQuery = true)
    List<ResBatchRepository.CResYears> findStartDateMonth(Long idxUser);

    @Query(value = "select comm.resAccountStartDate\n" +
            ", if(Date_Format(resAccountStartDate , '%Y%m') = Date_Format(startDay , '%Y%m'), resAccountStartDate, startDay) startDay\n" +
            ", comm.endDay as endDay\n" +
            ", comm.resAccount as resAccount\n" +
            ", comm.connectedId as connectedId\n" +
            ", comm.organization as  organization\n" +
            ", comm.resAccountDeposit as resAccountDeposit\n" +
            ", comm.resAccountCurrency as resAccountCurrency\n" +
            ", comm.nowMonth as nowMonth " +
            ", comm.errCode as errCode " +
            "from \n" +
            "( select \n" +
            "case  \n" +
            "when organization = 0003 then 201301 \n" +
            "when organization = 0007 then date_format(date_add(now(), INTERVAL - 5 year), '%Y%m%d')\n" +
            "when organization = 0020 then date_format(date_add(now(), INTERVAL - 12 month), '%Y%m%d')\n" +
            "when organization = 0027 then date_format(date_add(now(), INTERVAL - 6 month), '%Y%m%d')\n" +
            "when organization = 0048 then date_format(date_add(now(), INTERVAL - 5 year), '%Y%m%d')\n" +
            "when organization = 0081 then date_format(date_add(now(), INTERVAL - 12 month), '%Y%m%d')\n" +
            "else A.resAccountStartDate\n" +
            "end AS resAccountStartDate\n" +
            ", A.startDay\n" +
            ", A.endDay\n" +
            ", A.resAccount\n" +
            ", A.connectedId\n" +
            ", A.organization       \n" +
            ", A.resAccountDeposit \n" +
            ", A.resAccountCurrency \n" +
            ", A.nowMonth\n" +
            ", A.errCode\n" +
            "from (\n" +
            "select \n" +
            "(select errCode from ResBatchList where account = main.resAccount and Date_Format(startDate, '%Y%m') = Date_Format(main.startDay, '%Y%m') order by idx desc limit 1) errCode,\n" +
            "main.*, if(Date_Format(now() , '%Y%m') = Date_Format(main.startDay, '%Y%m'), 1, 0 ) as nowMonth from \n" +
            "(select if(y = Date_Format(resAccountStartDate , '%Y%m'), resAccountStartDate, concat(L.y,'01')) startDay\n" +
            ", if(y = Date_Format(now() , '%Y%m'), Date_Format(now() , '%Y%m%d'), date_format(last_day(concat(L.y,'01')),'%Y%m%d')) as endDay\n" +
            ", R.resAccountStartDate\n" +
            ", R.resAccount\n" +
            ", R.connectedId\n" +
            ", R.organization       \n" +
            ", R.resAccountDeposit \n" +
            ", R.resAccountCurrency \n" +
            "from (\n" +
            "select y from\n" +
            "(\n" +
            "SELECT date_format(  date_add(now(), INTERVAL - (a.a + (10 * b.a) + (100 * c.a)) month), '%Y%m') y\n" +
            "from (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as a\n" +
            "cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as b\n" +
            "cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as c\n" +
            ") a where a.y >  date_format(date_add(now(), INTERVAL - 10 year), '%Y%m')\n" +
            ") L\n" +
            "join ResAccount R \n" +
            "join ConnectedMng cm on R.connectedId = cm.connectedId and idxUser = :idxUser\n" +
            ") main\n" +
            ") A \n" +
            ") comm where ((errCode is null or errCode != 'CF-00000') and Date_Format(startDay , '%Y%m') >= Date_Format(resAccountStartDate , '%Y%m')) or ( :boolNow and nowMonth = 1) \n" +
            "order by startDay desc, endDay desc",nativeQuery = true)
    List<ResBatchRepository.CResYears> find10yearMonth(Long idxUser , Boolean boolNow);

    @Transactional
    @Modifying
    @Query("update ResBatch set endFlag = 1 where idxUser = :idxUser and endFlag = 0 ")
    int updateProcessIdx(@Param("idxUser") Long idxUser);

    public static interface CResBatchDto {
        String getMin();
        String getEndFlag();
        String getErrCode();
        String getErrMessage();
        String getResBatchType();
        String getTotal();
        String getProgressCnt();
        String getErrorCnt();
    }

    public static interface CResYears {
        String getStartDay();
        String getEndDay();
        String getResAccount();
        String getConnectedId();
        String getOrganization();
        String getResAccountDeposit();
        String getResAccountCurrency();
        String getNowMonth();
    }
}