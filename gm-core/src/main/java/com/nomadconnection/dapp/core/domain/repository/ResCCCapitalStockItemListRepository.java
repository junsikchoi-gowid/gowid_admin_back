package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResCCCapitalStockItemList;
import com.nomadconnection.dapp.core.domain.ResJointPartnerList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResCCCapitalStockItemListRepository extends JpaRepository<ResCCCapitalStockItemList, Long> {

}