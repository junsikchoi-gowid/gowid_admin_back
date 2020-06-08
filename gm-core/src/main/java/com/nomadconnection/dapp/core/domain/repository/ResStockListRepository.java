package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResStockList;
import com.nomadconnection.dapp.core.domain.ResTCntStockIssueList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResStockListRepository extends JpaRepository<ResStockList, Long> {

}