package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Corp;
import com.nomadconnection.dapp.core.domain.Risk;
import com.nomadconnection.dapp.core.domain.User;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiskRepository extends JpaRepository<Risk, Long>, AdminCustomRepository {

    @Query(value = "select ifnull(sum(if( R.errCode = 'CF-00000', 0, 1 )), 0)  " +
            " from ResBatchList R  " +
            " join (select account, max(idx) idx  from ResBatchList r where resBatchType = 1 " +
            " and idxResBatch = (SELECT idxResBatch FROM ResBatchList where idxUser = :idxUser order by idxResBatch desc limit 1)   " +
            " group by account) A on A.idx = R.idx",nativeQuery = true)
    Integer findErrCount(Long idxUser);

    @Query(value = "SELECT cardLimit FROM Risk where date_format(date,'%d') = 15 and idxUser = :idxUser and date <= :setDate order by date desc limit 1 ",nativeQuery = true)
    Double findCardLimitNow(Long idxUser, String setDate);

    @Query(value = "SELECT cardLimitNow FROM Risk where idxUser = :idxUser and date < :setDate order by date desc limit 1 ",nativeQuery = true)
    Double findCardLimitNowFirst(Long idxUser, String setDate);

    Optional<Risk> findByUserAndDate(User user, String Date);

    Optional<Risk> findByCorpAndDate(Corp corp, String Date);

    Page<Risk> findByCorp(Corp corp, Pageable pageable);
}
