package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResPurposeList;
import com.nomadconnection.dapp.core.domain.ResRegistrationHisList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResRegistrationHisListRepository extends JpaRepository<ResRegistrationHisList, Long> {

}