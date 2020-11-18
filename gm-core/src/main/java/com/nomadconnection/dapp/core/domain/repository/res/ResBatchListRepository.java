package com.nomadconnection.dapp.core.domain.repository.res;

import com.nomadconnection.dapp.core.domain.repository.querydsl.ResBatchListCustomRepository;
import com.nomadconnection.dapp.core.domain.res.ResBatchList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface ResBatchListRepository extends JpaRepository<ResBatchList, Long>, ResBatchListCustomRepository{
    ResBatchList findFirstByAccountOrderByUpdatedAtDesc(String ResAccount);

    interface ErrorResultDto {
        Long getIdxCorp();
        LocalDateTime getUpdatedAt();
        String getCorpName();
        String getBankName();
        String getAccount();
        String getResAccountDisplay();
        String getErrMessage();
        String getErrCode();
        String getTransactionId();
    }

    int countByErrCodeNotAndResBatchTypeAndIdxResBatch(String errCode, String resBatchType, Long idxResBatch);
}