package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResOneStocAmtList;
import com.nomadconnection.dapp.core.domain.ResUserAddrList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResOneStocAmtListRepository extends JpaRepository<ResOneStocAmtList, Long> {

}