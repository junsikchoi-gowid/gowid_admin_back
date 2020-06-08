package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResCCCapitalStockList;
import com.nomadconnection.dapp.core.domain.ResEtcList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResEtcListRepository extends JpaRepository<ResEtcList, Long> {

}