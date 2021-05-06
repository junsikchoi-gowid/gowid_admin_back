package com.nomadconnection.dapp.core.domain.repository.flow;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.flow.FlowTagMonth;
import com.nomadconnection.dapp.core.dto.flow.FlowReportExcelDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FlowTagMonthRepository extends JpaRepository<FlowTagMonth, Long> {

    @Query(" select new com.nomadconnection.dapp.core.dto.flow.FlowReportExcelDto(ftc.codeLv1, ftc.codeLv2, ftc.codeLv3, ftc.codeLv4, ftm.flowDate, ftm.flowTotal ) " +
            " from Corp c " +
            " left join FlowTagMonth ftm on c.idx = ftm.corp " +
            " left join FlowTagConfig ftc on ftc.idx = ftm.flowTagConfig " +
            " where c.idx = :idxCorp " +
            " and ftm.flowDate BETWEEN substr( :fromDate, 1, 6) and substr( :toDate, 1, 6) " +
            " order by ftc.codeLv1 desc, ftc.codeLv2 desc, ftc.codeLv3 desc, ftc.codeLv4 desc ")
    List<FlowReportExcelDto> findByCorpAndFlowDateBetween(@Param("idxCorp") Long idxCorp
            , @Param("fromDate") String fromDate
            , @Param("toDate") String toDate);

    Optional<FlowTagMonth> findTopByCorpAndFlowDate(Corp corp, String month);

    Optional<FlowTagMonth> findByCorpAndFlowDate(Corp corp, String month);

    @Transactional
    @Modifying
    @Query(value = "delete from FlowTagMonth  where idxCorp = :idxCorp and flowDate between :start and :end ", nativeQuery = true)
    void delFlowTagMonth(@Param("idxCorp") Long idxCorp, @Param("start") String start, @Param("end") String end);


    interface FlowReportExcelGroupDto {
        String getCodeLv1();
        String getCodeLv3();
        String getCodeLv4();
        Double getBefore3();
        Double getBefore2();
        Double getBefore1();
        Double getBefore0();
        Double getBeforesum();
        Double getLv3Sum();
        Double getLv2Sum();
        Double getLv1Sum();
        Double getLv0Sum();
    }

    @Query( value=" select codeLv1, codeLv3, codeLv4 , before3, before2, before1, before0  " +
            "    , before3+before2+before1+before0 as beforesum  " +
            "    , (  select ifnull(sum(flowTotal),0) from FlowTagMonth ft join FlowTagConfig fc on ft.idxFlowTag = fc.idx " +
            "        where flowDate = date_format(  date_add(str_to_date(:searchDate, '%Y%m%d'), INTERVAL - 3 month), '%Y%m')  " +
            "            and ft.idxCorp = :idxCorp " +
            "            and fc.codeLv1 = az.codeLv1 " +
            "            and fc.codeLv3 = az.codeLv3 ) as lv3Sum " +
            "    , (  select ifnull(sum(flowTotal),0) from FlowTagMonth ft join FlowTagConfig fc on ft.idxFlowTag = fc.idx " +
            "        where flowDate = date_format(  date_add(str_to_date(:searchDate, '%Y%m%d'), INTERVAL - 2 month), '%Y%m')  " +
            "            and ft.idxCorp = :idxCorp " +
            "            and fc.codeLv1 = az.codeLv1 " +
            "            and fc.codeLv3 = az.codeLv3 ) as lv2Sum " +
            "    , (  select ifnull(sum(flowTotal),0) from FlowTagMonth ft join FlowTagConfig fc on ft.idxFlowTag = fc.idx " +
            "        where flowDate = date_format(  date_add(str_to_date(:searchDate, '%Y%m%d'), INTERVAL - 1 month), '%Y%m')  " +
            "            and ft.idxCorp = :idxCorp " +
            "            and fc.codeLv1 = az.codeLv1 " +
            "            and fc.codeLv3 = az.codeLv3 ) as lv1Sum " +
            "    , (  select ifnull(sum(flowTotal),0) from FlowTagMonth ft join FlowTagConfig fc on ft.idxFlowTag = fc.idx " +
            "        where flowDate = date_format(  date_add(str_to_date(:searchDate, '%Y%m%d'), INTERVAL - 0 month), '%Y%m')  " +
            "            and ft.idxCorp = :idxCorp " +
            "            and fc.codeLv1 = az.codeLv1 " +
            "            and fc.codeLv3 = az.codeLv3 ) as lv0Sum " +
            "from ( " +
            "select codeLv1, codeLv3, codeLv4 " +
            "    , sum(case when flowDate = date_format(  date_add(str_to_date(:searchDate, '%Y%m%d'), INTERVAL - 3 month), '%Y%m') then flowTotal else 0 end ) before3 " +
            "    , sum(case when flowDate = date_format(  date_add(str_to_date(:searchDate, '%Y%m%d'), INTERVAL - 2 month), '%Y%m') then flowTotal else 0 end ) before2 " +
            "    , sum(case when flowDate = date_format(  date_add(str_to_date(:searchDate, '%Y%m%d'), INTERVAL - 1 month), '%Y%m') then flowTotal else 0 end ) before1 " +
            "    , sum(case when flowDate = date_format(  date_add(str_to_date(:searchDate, '%Y%m%d'), INTERVAL - 0 month), '%Y%m') then flowTotal else 0 end ) before0 " +
            "    from Corp c " +
            "    left join FlowTagMonth ftm on c.idx = ftm.idxCorp " +
            "    left join FlowTagConfig ftc on ftm.idxFlowTag = ftc.idx " +
            "where c.idx = :idxCorp " +
            "    and ftm.flowDate BETWEEN date_format(  date_add(str_to_date(:searchDate, '%Y%m%d'), INTERVAL - 3 month), '%Y%m') and substr(:searchDate, 1, 6) " +
            "group by codeLv1, codeLv3, codeLv4 " +
            "  ) az " +
            "group by codeLv1, codeLv3, codeLv4 , before3, before2, before1, before0  " +
            "order by codeLv1 asc, lv0Sum desc, lv1Sum desc, lv2Sum desc, lv3Sum desc, codeLv3 asc, before0 desc, before1 desc, before2 desc, before3 desc ", nativeQuery = true)
    List<FlowReportExcelGroupDto> searchExcelData(@Param("idxCorp") Long idxCorp, @Param("searchDate") String searchDate);
}