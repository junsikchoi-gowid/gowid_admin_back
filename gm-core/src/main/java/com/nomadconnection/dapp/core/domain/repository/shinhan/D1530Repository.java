package com.nomadconnection.dapp.core.domain.repository.shinhan;

import com.nomadconnection.dapp.core.domain.shinhan.D1530;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface D1530Repository extends JpaRepository<D1530, Long> {
    Optional<D1530> findFirstByIdxCorpOrderByUpdatedAtDesc(long idxCorp);

    @Transactional
    @Modifying
    @Query("delete from D1530  where idxCorp = :idxCorp")
    void deleteByCorpIdx(@Param("idxCorp") Long idxCorp);
}
