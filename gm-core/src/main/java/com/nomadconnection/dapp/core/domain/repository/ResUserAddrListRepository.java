package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResCompanyNmList;
import com.nomadconnection.dapp.core.domain.ResUserAddrList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResUserAddrListRepository extends JpaRepository<ResUserAddrList, Long> {

}