package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResCCCapitalStockList;
import com.nomadconnection.dapp.core.domain.ResTypeStockContentList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResCCCapitalStockListRepository extends JpaRepository<ResCCCapitalStockList, Long> {

}