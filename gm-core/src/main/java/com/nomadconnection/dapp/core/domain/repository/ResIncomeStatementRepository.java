package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResBalanceSheet;
import com.nomadconnection.dapp.core.domain.ResIncomeStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResIncomeStatementRepository extends JpaRepository<ResIncomeStatement, Long> {

}