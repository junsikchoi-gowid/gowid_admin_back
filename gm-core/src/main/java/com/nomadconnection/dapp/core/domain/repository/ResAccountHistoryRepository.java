package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResAccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ResAccountHistoryRepository extends JpaRepository<ResAccountHistory, Long> {

    @Transactional
    @Modifying
    @Query("delete from ResAccountHistory c where c.resAccount = :resAccount and c.resAccountTrDate between :startDate and :endDate")
    void deleteResAccountTrDate(@Param("resAccount") String resAccount, @Param("startDate") String startDate, @Param("endDate") String endDate );


    @Query(value = "select sum(ifnull (resAccountIn,0)) sumResAccountIn , sum( ifnull (resAccountOut,0)) sumResAccountOut\n" +
            "\tfrom ResAccountHistory where resAccountTrDate between :start and :end  \n" +
            "\t\tand resAccount in " +
            " ( select resAccount from ResAccount where connectedId in ( select connectedId from ConnectedMng where idxUser = :idxUser)" +
            "   and resAccountDeposit in ('10','11','12','13','14'))"
            ,nativeQuery = true)
    CMonthInOutSumDto findMonthInOutSum(@Param("start") String start, @Param("end") String end, @Param("idxUser")Long idxUser);

    public static interface CMonthInOutSumDto {
        Long getSumResAccountIn();
        Long getSumResAccountOut();
    }
}