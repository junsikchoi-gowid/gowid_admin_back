package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResConvertibleBondList;
import com.nomadconnection.dapp.core.domain.ResManagerList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResConvertibleBondListRepository extends JpaRepository<ResConvertibleBondList, Long> {

}