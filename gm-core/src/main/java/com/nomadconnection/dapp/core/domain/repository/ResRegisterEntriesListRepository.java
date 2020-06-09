package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResRegisterEntriesList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResRegisterEntriesListRepository extends JpaRepository<ResRegisterEntriesList, Long> {
    Optional<ResRegisterEntriesList> findTopByIdxCorpOrOrderByIdxDesc(Long idxCorp);
}