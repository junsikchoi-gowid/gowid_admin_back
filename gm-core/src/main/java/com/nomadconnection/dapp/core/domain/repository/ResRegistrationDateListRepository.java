package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResChangeDateList;
import com.nomadconnection.dapp.core.domain.ResRegistrationDateList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResRegistrationDateListRepository extends JpaRepository<ResRegistrationDateList, Long> {

}