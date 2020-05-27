package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResConvertibleBondList;
import com.nomadconnection.dapp.core.domain.ResWarrantBondList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResWarrantBondListRepository extends JpaRepository<ResWarrantBondList, Long> {

}