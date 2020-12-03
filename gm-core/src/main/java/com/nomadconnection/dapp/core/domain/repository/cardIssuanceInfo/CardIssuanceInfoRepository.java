package com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo;

import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CardIssuanceInfoRepository extends JpaRepository<CardIssuanceInfo, Long> {
    Optional<CardIssuanceInfo> findTopByUserAndDisabledFalseOrderByIdxDesc(User user);
    CardIssuanceInfo getTopByUserAndDisabledFalseOrderByIdxDesc(User user);
    Optional<CardIssuanceInfo> findByIdx(Long idx);
    Optional<CardIssuanceInfo> findByCorpAndDisabledFalseOrderByIdxDesc(Corp corp);

    @Query(value = "select idx FROM CardIssuanceInfo where idxUser = :idxUser and disabled = false", nativeQuery = true)
    List<Long> findAllIdxByUserIdx(@Param("idxUser") Long idxUser);

    @Query(value = "select * FROM CardIssuanceInfo where idxUser = :idxUser and disabled = false order by idx", nativeQuery = true)
    Optional<CardIssuanceInfo> findTopByUserAndDisabledFalseOrderByIdxDesc(@Param("idxUser") Long idxUser);

    @Transactional
    @Modifying
    @Query("delete from CardIssuanceInfo  where idxuser = :idxUser and disabled = false")
    void deleteAllByUserIdx(@Param("idxUser") Long idxUser);
}
