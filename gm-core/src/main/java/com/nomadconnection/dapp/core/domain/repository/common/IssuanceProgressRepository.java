package com.nomadconnection.dapp.core.domain.repository.common;

import com.nomadconnection.dapp.core.domain.common.IssuanceProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface IssuanceProgressRepository extends JpaRepository<IssuanceProgress, Long> {
    @Transactional
    @Modifying
    @Query("delete from IssuanceProgress  where userIdx = :idxUser")
    void deleteAllByUserIdx(@Param("idxUser") Long idxUser);
}
