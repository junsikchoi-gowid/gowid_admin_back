package com.nomadconnection.dapp.core.domain.repository.res;

import com.nomadconnection.dapp.core.domain.res.ResAccountHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResAccountHistoryRepository extends JpaRepository<ResAccountHistory, Long> {

    @Transactional
    @Modifying
    @Query("delete from ResAccountHistory c where c.resAccount = :resAccount and c.resAccountTrDate between :startDate and :endDate")
    void deleteResAccountTrDate(@Param("resAccount") String resAccount, @Param("startDate") String startDate, @Param("endDate") String endDate );

    ResAccountHistory findTopByResAccountAndResAccountInEqualsAndResAccountOutAndResAccountTrDateAndResAccountTrTimeAndResAfterTranBalanceAndResAccountCurrency(String resAccount, String resAccountIn, String resAccountOut, String resAccountTrDate, String resAccountTrTime, String resAfterTranBalance, String accountCurrency);






    interface CMonthInOutSumDto {
        Long getSumResAccountIn();
        Long getSumResAccountOut();
    }

    @Query(value = "select sum(ifnull (resAccountIn,0)) sumResAccountIn , sum( ifnull (resAccountOut,0)) sumResAccountOut\n" +
            "\tfrom ResAccountHistory where resAccountTrDate between :start and :end  \n" +
            "\t\tand resAccount in " +
            " ( select resAccount from ResAccount where connectedId in ( select connectedId from ConnectedMng where idxUser = :idxUser)" +
            "   and resAccountDeposit in ('10','11','12','13','14'))"
            ,nativeQuery = true)
    CMonthInOutSumDto findMonthInOutSum(@Param("start") String start, @Param("end") String end, @Param("idxUser")Long idxUser);

    List<ResAccountHistory> findByResAccountAndResAccountTrDateBetweenAndResAccountInGreaterThanAndResAccountDesc1(String resAccount,String startDate,String endDate,String resAccountIn,String desc);

    List<ResAccountHistory> findByResAccountAndResAccountTrDateBetweenAndResAccountInGreaterThanAndResAccountDesc2(String resAccount,String startDate,String endDate,String resAccountIn,String desc);

    List<ResAccountHistory> findByResAccountAndResAccountTrDateBetweenAndResAccountInGreaterThanAndResAccountDesc3(String resAccount,String startDate,String endDate,String resAccountIn,String desc);

    List<ResAccountHistory> findByResAccountAndResAccountTrDateBetweenAndResAccountInGreaterThanAndResAccountDesc4(String resAccount,String startDate,String endDate,String resAccountIn,String desc);

    @Query(value = "select sum(ifnull (resAccountIn,0)) from ResAccountHistory where idx in :idx ", nativeQuery = true)
    String sumCeoInBalance(List<Long> idx);

    @Override
    Optional<ResAccountHistory> findById(Long idxResAccountHistory);


    @Query(value = "SELECT R.* FROM ResAccountHistory R WHERE " +
            " resAccountTrDate >= :from AND resAccountTrDate <= :to" +
            " and resAccount IN (:resAccountList)" +
            " and (:searchWord is null or (concat_ws(resAccountDesc1 , resAccountDesc2 , resAccountDesc3 , resAccountDesc4, memo, tagValue )  like concat('%',:searchWord,'%') ))",
            countQuery = "SELECT R.* FROM ResAccountHistory R WHERE " +
                    " resAccount IN (:resAccountList) " +
                    " and (:searchWord is null or (concat_ws(resAccountDesc1 , resAccountDesc2 , resAccountDesc3 , resAccountDesc4, memo, tagValue )  like concat('%',:searchWord,'%') ))",
            nativeQuery = true)
    Page<ResAccountHistory> searchAllResAccountHistoryLikeList(
            @Param("resAccountList") List<String> resAccountList,
            @Param("from") String from, @Param("to") String to,
            @Param("searchWord") String searchWord,
            @Param("pageable") Pageable pageable);

    @Query(value = "SELECT R.* FROM ResAccountHistory R WHERE " +
            " resAccountTrDate >= :from AND resAccountTrDate <= :to" +
            " and resAccount IN (:resAccountList) AND resAccountIn = '0' AND resAccountOut <> '0' " +
            " and (:searchWord is null or (concat_ws(resAccountDesc1 , resAccountDesc2 , resAccountDesc3 , resAccountDesc4, memo, tagValue )  like concat('%',:searchWord,'%') ))",
            countQuery = "SELECT R.** FROM ResAccountHistory R WHERE " +
                    " resAccount IN (:resAccountList) AND resAccountIn = '0' AND resAccountOut <> '0' " +
                    " and (:searchWord is null or (concat_ws(resAccountDesc1 , resAccountDesc2 , resAccountDesc3 , resAccountDesc4, memo, tagValue )  like concat('%',:searchWord,'%') ))",
            nativeQuery = true)
    Page<ResAccountHistory> searchInResAccountHistoryLikeList(
            @Param("resAccountList") List<String> resAccountList,
            @Param("from") String from, @Param("to") String to,
            @Param("searchWord") String searchWord,
            @Param("pageable") Pageable pageable);

    @Query(value = "SELECT R.* FROM ResAccountHistory R WHERE " +
            " resAccountTrDate >= :from AND resAccountTrDate <= :to" +
            " and resAccount IN (:resAccountList) AND resAccountIn = '0' AND resAccountOut <> '0' " +
            " and (:searchWord is null or (concat_ws(resAccountDesc1 , resAccountDesc2 , resAccountDesc3 , resAccountDesc4, memo, tagValue )  like concat('%',:searchWord,'%') ))",
            countQuery = "SELECT R.* FROM ResAccountHistory R WHERE " +
                    " resAccount IN (:resAccountList) AND resAccountIn <> '0' AND resAccountOut ='0' " +
                    " and (:searchWord is null or (concat_ws(resAccountDesc1 , resAccountDesc2 , resAccountDesc3 , resAccountDesc4, memo, tagValue )  like concat('%',:searchWord,'%') ))",
            nativeQuery = true)
    Page<ResAccountHistory> searchOutResAccountHistoryLikeList(
            @Param("resAccountList") List<String> resAccountList,
            @Param("from") String from, @Param("to") String to,
            @Param("searchWord") String searchWord,
            @Param("pageable") Pageable pageable);

    interface CMonthStaticsDto {
        Long getFlowIn();
        Long getFlowOut();
        Long getIdxFlowTagConfig();
        String getMonth();
    }

    @Query(value = "SELECT "+
            " SUM(IFNULL(CAST(resAccountIn AS SIGNED ),0)) AS flowIn, " +
            " SUM(IFNULL(CAST(resAccountOut AS SIGNED ),0)) AS flowOut,  " +
            " idxFlowTagConfig,  " +
            " substr(resAccountTrDate,1,6) AS month  " +
            " FROM ResAccountHistory " +
            " WHERE resAccount IN (:resAccountList) " +
            " AND resAccountTrDate > (:resAccountTrDate) " +
            " GROUP BY idxFlowTagConfig, SUBSTR(resAccountTrDate,1,6)" +
            " ORDER BY month ",
            nativeQuery = true)
    List<CMonthStaticsDto> monthStatics(@Param("resAccountList") List<String> resAccountList, @Param("resAccountTrDate") String resAccountTrDate);

    interface CMonthInOutStaticsDto {
        Long getFlowIn();
        Long getFlowOut();
        String getMonth();
    }
    @Query(value = "SELECT  " +
            " SUM(IFNULL(CAST(resAccountIn AS SIGNED ),0)) AS flowIn, " +
            " SUM(IFNULL(CAST(resAccountOut AS SIGNED ),0)) AS flowOut,  " +
            " SUBSTR(resAccountTrDate,1,6) AS month  " +
            " FROM ResAccountHistory " +
            " WHERE resAccount IN (:resAccountList) " +
            " AND resAccountTrDate >= :from AND resAccountTrDate <= :to" +
            " GROUP BY SUBSTR(resAccountTrDate,1,6) " +
            " ORDER BY month ",
            nativeQuery = true)
    CMonthInOutStaticsDto monthInOutStatics(@Param("resAccountList") List<String> resAccountList
            , @Param("from") String from
            , @Param("to") String to);




}