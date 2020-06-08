package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResStockItemList;
import com.nomadconnection.dapp.core.domain.ResStockList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResStockItemListRepository extends JpaRepository<ResStockItemList, Long> {

}