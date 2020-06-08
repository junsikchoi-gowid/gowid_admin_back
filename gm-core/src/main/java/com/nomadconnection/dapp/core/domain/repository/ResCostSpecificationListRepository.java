package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResCostSpecificationList;
import com.nomadconnection.dapp.core.domain.ResIncomeStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResCostSpecificationListRepository extends JpaRepository<ResCostSpecificationList, Long> {

}