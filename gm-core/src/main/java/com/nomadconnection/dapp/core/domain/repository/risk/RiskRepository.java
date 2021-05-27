package com.nomadconnection.dapp.core.domain.repository.risk;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.domain.risk.Risk;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskRepository extends JpaRepository<Risk, Long>, AdminCustomRepository {

    @Query(value = "SELECT cardLimitNow FROM Risk where idxUser = :idxUser and date <= :setDate order by date desc limit 1 ",nativeQuery = true)
    Double findCardLimitNowFirst(@Param("idxUser") Long idxUser,
                                 @Param("setDate") String setDate);

    List<Risk> findByUser(User user);
    Optional<Risk> findByUserAndDate(User user, String Date);

    Optional<Risk> findByCorpAndDate(Corp corp, String Date);

    Optional<Risk> findTopByUserAndDateLessThanEqualOrderByDateDesc(User user, String Date);

    Page<Risk> findByCorp(Corp corp, Pageable pageable);

    @Transactional
    @Modifying
    @Query("delete from Risk  where idxCorp = :idxCorp")
    void deleteByCorpIdx(@Param("idxCorp") Long idxCorp);
}
