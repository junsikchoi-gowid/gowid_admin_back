package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResStandardFinancialList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResStandardFinancialListRepository extends JpaRepository<ResStandardFinancialList, Long> {

}