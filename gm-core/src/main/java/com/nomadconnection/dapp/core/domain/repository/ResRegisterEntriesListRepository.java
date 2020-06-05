package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResRegisterEntriesList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResRegisterEntriesListRepository extends JpaRepository<ResRegisterEntriesList, Long> {

}