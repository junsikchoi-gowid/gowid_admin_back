package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.StockholderFile;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.StockholderFileType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockholderFileRepository extends JpaRepository<StockholderFile, Long> {
    List<StockholderFile> findAllByCorp(Corp corp);

    List<StockholderFile> findAllByCorpAndType(Corp corp, StockholderFileType type);
}
