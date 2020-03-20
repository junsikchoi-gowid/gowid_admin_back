package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResBatch;
import com.nomadconnection.dapp.core.domain.ResBatchList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    @Query(value = "select \n" +
            " resAccount , resAccountDeposit , connectedId, organization \n" +
            "    , Date_Format(now() , '%Y%m%d') toDay, Date_Format( DATE_SUB(now(), INTERVAL 1 year)  , '%Y%m%d') preYear" +
            " , ifnull((SELECT Date_Format(updatedAt, '%Y%m%d') from ResBatchList \n" +
            " where idxResBatch = ( select idx from ResBatch where idxUser = :idxUser and endFlag = 1 order by updatedAt desc limit 1)\n" +
            "    and account = R.resAccount and resBatchType = 1 and errcode = 'CF-00000' order by updatedAt desc limit 1), resAccountStartDate) successDay\n" +
            " from ResAccount R where connectedId in (select connectedId from ConnectedMng where idxUser = :idxUser)",nativeQuery = true)
    List<ResBatchRepository.CResStartDate> findStartDate(Long idxUser);

    @Query(value = "select \n" +
            "\tif(lastSuccessDay is null, if(Date_Format(resAccountStartDate, '%Y') = b.y , resAccountStartDate , concat(b.y,'0101')), lastSuccessDay ) startDay\n" +
            "    , if(Date_Format(now(), '%Y') = b.y ,endDay, concat(b.y,'1231')) endDay \n" +
            "    , a.resAccount  resAccount" +
            "    , a.connectedId  connectedId" +
            "    , a.organization  organization" +
            "    , a.resAccountDeposit  resAccountDeposit" +
            "\tfrom \t\n" +
            "    ( select resAccount , resAccountDeposit , connectedId, organization \n" +
            "\t\t, (SELECT startDate from ResBatchList\n" +
            "\t\t\twhere idxResBatch = ( select idx from ResBatch where idxUser = :idxUser and endFlag = 1 order by updatedAt asc limit 1)\n" +
            "\t\t\tand account = R.resAccount and errcode = 'CF-00000' order by updatedAt asc limit 1) firstSuccessDay\n" +
            "\t\t, (SELECT endDate from ResBatchList\n" +
            "\t\t\twhere idxResBatch = ( select idx from ResBatch where idxUser = :idxUser and endFlag = 1 order by updatedAt desc limit 1)\n" +
            "\t\t\tand account = R.resAccount and errcode = 'CF-00000' order by updatedAt desc limit 1) lastSuccessDay           \n" +
            "\t\t, resAccountStartDate\n" +
            "\t\t, Date_Format(now() , '%Y%m%d') endDay\n" +
            "\tfrom ResAccount R where connectedId in (select connectedId from ConnectedMng where idxUser = :idxUser)\n" +
            "    ) a\n" +
            "\tjoin \n" +
            "\t(SELECT date_format(  date_add(now(), INTERVAL - t0 year), '%Y') y\n" +
            "\t\tfrom (SELECT 0 t0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10)\n" +
            "        b\n" +
            "\t) b\n" +
            "    where y >= Date_Format(if( a.resAccountStartDate = a.firstSuccessDay , endDay, resAccountStartDate) , '%Y')",nativeQuery = true)
    List<ResBatchRepository.CResYears> find10year(Long idxUser);

    @Query(value = "select main.*, if(Date_Format(now() , '%Y%m') = Date_Format(main.startDay, '%Y%m'), 1, 0 ) as nowMonth from \n" +
            "\t(select if(y = Date_Format(resAccountStartDate , '%Y%m'), resAccountStartDate, concat(L.y,'01')) startDay    \n" +
            "\t, if(y = Date_Format(now() , '%Y%m'), Date_Format(now() , '%Y%m%d'), date_format(last_day(concat(L.y,'01')),'%Y%m%d')) as endDay\n" +
            "\t, R.resAccountStartDate\n" +
            "\t, R.resAccount\n" +
            "\t, R.connectedId\n" +
            "\t, R.organization       \n" +
            "\t, R.ResAccountDeposit\n" +
            "\tfrom (\n" +
            "\tselect y from\n" +
            "\t(\n" +
            "\tSELECT date_format(  date_add(now(), INTERVAL - (a.a + (10 * b.a) + (100 * c.a)) month), '%Y%m') y\n" +
            "\tfrom (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as a\n" +
            "\tcross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as b\n" +
            "\tcross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as c\n" +
            "\t) a where a.y >  date_format(date_add(now(), INTERVAL - 12 month), '%Y%m')\n" +
            "\t) L\n" +
            "\tjoin ResAccount R \n" +
            "\tjoin ConnectedMng cm on R.connectedId = cm.connectedId and idxUser = 23\n" +
            ") main \n" +
            "\tleft join ResBatchList b on b.account = main.resAccount\n" +
            "where endDate = Date_Format(now() , '%Y%m%d') or idx is null",nativeQuery = true)
    List<ResBatchRepository.CResYears> findStartDateMonth(Long idxUser);

    @Query(value = "select distinct main.* \n" +
            "from (select L.y\n" +
            "\t\t, if(y = Date_Format(resAccountStartDate , '%Y%m'), resAccountStartDate, concat(L.y,'01')) startDay    \n" +
            "\t\t, date_format(last_day(concat(L.y,'01')),'%Y%m%d') as endDay\n" +
            "\t\t, R.resAccount\n" +
            "\t\t, R.connectedId\n" +
            "\t\t, R.organization , R.ResAccountDeposit \n" +
            "\t\tfrom ResAccount R \n" +
            "\t\t\tjoin ConnectedMng cm on R.connectedId = cm.connectedId and idxUser = :idxUser             \n" +
            "\t\tcross join \n" +
            "\t\t(\n" +
            "\t\tselect y from\n" +
            "\t\t\t(\n" +
            "\t\t\tSELECT date_format(  date_add(now(), INTERVAL - (a.a + (10 * b.a) + (100 * c.a)) month), '%Y%m') y\n" +
            "\t\t\t\t\tfrom (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as a\n" +
            "\t\t\t\tcross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as b\n" +
            "\t\t\t\tcross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as c\n" +
            "\t\t\t) a where a.y >  date_format(date_add(now(), INTERVAL - 10 year), '%Y%m')\n" +
            "\t\t) L\n" +
            "\t\twhere y >= Date_Format(resAccountStartDate , '%Y%m') and y <= date_format(  date_add(now(), INTERVAL - 11 month), '%Y%m')\n" +
            "    ) main \n" +
            "\t\tleft join ResBatchList b on main.startDay = b.startDate and main.endDay = b.endDate and main.resAccount = b.account\n" +
            "\twhere idx is null",nativeQuery = true)
    List<ResBatchRepository.CResYears> find10yearMonth(Long idxUser);

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

    public static interface CResStartDate {
        String getPreYear();
        String getResAccount();
        String getConnectedId();
        String getOrganization();
        String getResAccountDeposit();
        String getToDay();
        String getSuccessDay();
    }

    public static interface CResYears {
        String getStartDay();
        String getEndDay();
        String getResAccount();
        String getConnectedId();
        String getOrganization();
        String getResAccountDeposit();
        String getNowMonth();
    }
}