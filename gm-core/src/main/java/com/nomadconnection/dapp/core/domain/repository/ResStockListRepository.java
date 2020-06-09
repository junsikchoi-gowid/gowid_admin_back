package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResStockList;
import com.nomadconnection.dapp.core.domain.ResTCntStockIssueList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResStockListRepository extends JpaRepository<ResStockList, Long> {
    Optional<ResStockList> findTopByIdxParentOrderByIdxDesc(Long idxParent);
}