package com.nomadconnection.dapp.core.domain.repository.corp;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.ManagerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ManagerRepository extends JpaRepository<ManagerInfo, Long> {
    ManagerInfo getByCardIssuanceInfo(CardIssuanceInfo cardIssuanceInfo);

    @Transactional
    @Modifying
    @Query("delete from ManagerInfo where idxCardIssuanceInfo in :idxCardIssuanceInfo")
    void deleteAllByCardIssuanceInfoIdx(@Param("idxCardIssuanceInfo") List<Long> idxCardIssuanceInfo);
}

