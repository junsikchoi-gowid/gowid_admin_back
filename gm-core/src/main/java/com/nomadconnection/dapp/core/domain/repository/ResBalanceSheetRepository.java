package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResBalanceSheet;
import com.nomadconnection.dapp.core.domain.ResCEOList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResBalanceSheetRepository extends JpaRepository<ResBalanceSheet, Long> {

}