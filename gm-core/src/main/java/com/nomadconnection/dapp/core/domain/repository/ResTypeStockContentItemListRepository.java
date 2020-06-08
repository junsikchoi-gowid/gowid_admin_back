package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResChangeDateList;
import com.nomadconnection.dapp.core.domain.ResTypeStockContentItemList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResTypeStockContentItemListRepository extends JpaRepository<ResTypeStockContentItemList, Long> {

}