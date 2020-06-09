package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResCorpEstablishDateList;
import com.nomadconnection.dapp.core.domain.ResEtcList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResCorpEstablishDateListRepository extends JpaRepository<ResCorpEstablishDateList, Long> {
    Optional<ResCorpEstablishDateList> findTopByIdxParentOrderByIdxDesc(Long idxParent);
}