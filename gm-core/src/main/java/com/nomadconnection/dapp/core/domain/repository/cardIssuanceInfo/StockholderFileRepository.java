package com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.StockholderFile;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.StockholderFileType;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StockholderFileRepository extends JpaRepository<StockholderFile, Long> {
    List<StockholderFile> findAllByCorpAndType(Corp corp, StockholderFileType type);

    @Transactional
    @Modifying
    @Query("delete from StockholderFile  where idxCardIssuanceInfo in (:idxCardIssuanceInfo)")
    void deleteAllByCardIssuanceInfoIdx(@Param("idxCardIssuanceInfo") List<Long> idxCardIssuanceInfo);
}
