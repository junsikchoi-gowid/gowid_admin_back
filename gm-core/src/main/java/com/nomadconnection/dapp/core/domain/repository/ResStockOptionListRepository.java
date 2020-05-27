package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResParticipatingBondList;
import com.nomadconnection.dapp.core.domain.ResStockOptionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResStockOptionListRepository extends JpaRepository<ResStockOptionList, Long> {

}