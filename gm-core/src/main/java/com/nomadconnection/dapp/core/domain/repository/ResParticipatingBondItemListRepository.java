package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResParticipatingBondItemList;
import com.nomadconnection.dapp.core.domain.ResRegistrationDateList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResParticipatingBondItemListRepository extends JpaRepository<ResParticipatingBondItemList, Long> {

}