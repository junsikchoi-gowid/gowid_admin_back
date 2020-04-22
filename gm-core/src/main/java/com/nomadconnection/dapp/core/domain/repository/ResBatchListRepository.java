package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ResBatchList;
import com.nomadconnection.dapp.core.domain.repository.querydsl.ResBatchListCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ResBatchListRepository extends JpaRepository<ResBatchList, Long> , ResBatchListCustomRepository {
    ResBatchList findFirstByAccountOrderByUpdatedAtDesc(String ResAccount);
}