package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResCorpEstablishDateList;
import com.nomadconnection.dapp.core.domain.ResRegistrationRecReasonList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResRegistrationRecReasonListRepository extends JpaRepository<ResRegistrationRecReasonList, Long> {

}