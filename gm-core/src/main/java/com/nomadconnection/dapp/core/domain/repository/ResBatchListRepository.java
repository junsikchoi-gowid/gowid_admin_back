package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResBatchList;
import com.nomadconnection.dapp.core.domain.repository.querydsl.ResBatchListCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface ResBatchListRepository extends JpaRepository<ResBatchList, Long>  {
    ResBatchList findFirstByAccountOrderByUpdatedAtDesc(String ResAccount);

    @Query(
            value = "select * from (select  \n" +
                    "        corp1_.idx as idxCorp,\n" +
                    "        d.updatedAt as updatedAt,\n" +
                    "        corp1_.resCompanyNm as corpName,\n" +
                    "        commoncode2_.value1 as bankName,\n" +
                    "        d.account as account,\n" +
                    "        d.errMessage as errMessage,\n" +
                    "        d.errCode as errCode,\n" +
                    "        d.transactionId as transactionId \n" +
                    "    from\n" +
                    "        ResBatchList d \n" +
                    "    inner join\n" +
                    "        Corp corp1_ \n" +
                    "            on (\n" +
                    "                corp1_.idxUser=d.idxUser\n" +
                    "            ) \n" +
                    "    left outer join\n" +
                    "        CommonCodeDetail commoncode2_ \n" +
                    "            on (\n" +
                    "                commoncode2_.code= 'bank_1'\n" +
                    "                and commoncode2_.code1=d.bank \n" +
                    "            )" +
                    ") d where (corpName like concat('%',:searchCorpName,'%') or :searchCorpName is null ) " +
                    " and ( errCode = :errCode or :errCode is null) " +
                    " and ( updatedAt > date_format( :strDate,  '%Y%m%d')) " +
                    " and ( transactionId = :transactionId or :transactionId is null) ",
            countQuery = "select count(*) from (select  \n" +
                    "        corp1_.idx as idxCorp,\n" +
                    "        d.updatedAt as updatedAt,\n" +
                    "        corp1_.resCompanyNm as corpName,\n" +
                    "        commoncode2_.value1 as bankName,\n" +
                    "        d.account as account,\n" +
                    "        d.errMessage as errMessage,\n" +
                    "        d.errCode as errCode,\n" +
                    "        d.transactionId as transactionId \n" +
                    "    from\n" +
                    "        ResBatchList d \n" +
                    "    inner join\n" +
                    "        Corp corp1_ \n" +
                    "            on (\n" +
                    "                corp1_.idxUser=d.idxUser\n" +
                    "            ) \n" +
                    "    left outer join\n" +
                    "        CommonCodeDetail commoncode2_ \n" +
                    "            on (\n" +
                    "                commoncode2_.code= 'bank_1'\n" +
                    "                and commoncode2_.code1=d.bank \n" +
                    "            )" +
                    ") d where (corpName like concat('%',:searchCorpName,'%') or :searchCorpName is null ) " +
                    " and ( errCode like concat('%',:errCode , '%') or :errCode is null) " +
                    " and ( updatedAt > date_format( :strDate,  '%Y%m%d')) " +
                    " and ( transactionId = :transactionId or :transactionId is null) ",
            nativeQuery = true
    )
    Page<ErrorResultDto> errorList(String searchCorpName, String errCode, String transactionId,String strDate, Pageable pageable);

    public static interface ErrorResultDto {
        Long getIdxCorp();
        LocalDateTime getUpdatedAt();
        String getCorpName();
        String getBankName();
        String getAccount();
        String getErrMessage();
        String getErrCode();
        String getTransactionId();
    }
}