package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResCostSpecificationList;
import com.nomadconnection.dapp.core.domain.ResFinancialStatementList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResFinancialStatementListRepository extends JpaRepository<ResFinancialStatementList, Long> {

}