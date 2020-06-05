package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResIncompetenceReasonList;
import com.nomadconnection.dapp.core.domain.ResJointPartnerList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResJointPartnerListRepository extends JpaRepository<ResJointPartnerList, Long> {

}