package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.shinhan.D1100;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface D1100Repository extends JpaRepository<D1100, Long> {
    D1100 getTopByIdxCorpOrderByIdxDesc(Long idxCorp);

    Optional<D1100> findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);

    @Transactional
    @Modifying
    @Query("delete from D1100  where idxCorp = :idxCorp")
    void deleteByCorpIdx(@Param("idxCorp") Long idxCorp);
}
