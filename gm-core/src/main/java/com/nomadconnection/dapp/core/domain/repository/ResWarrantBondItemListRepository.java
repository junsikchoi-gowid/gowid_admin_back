package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResConvertibleBondItemList;
import com.nomadconnection.dapp.core.domain.ResWarrantBondItemList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResWarrantBondItemListRepository extends JpaRepository<ResWarrantBondItemList, Long> {

}