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

    @Query(value = "SELECT cardLimit, date FROM Risk Where Date = date_format(date_add(now(), interval -1 day),'%Y%m%d') and date_format(now(),'%d') = 15 and  date_format(now(),'%H') < 5 ",nativeQuery = true)
    Double findCardLimitNow(Long idxUser);

    Optional<Risk> findByUserAndDate(User user, String Date);

    Optional<Risk> findByCorpAndDate(Corp corp, String Date);

    Page<Risk> findByCorp(Corp corp, Pageable pageable);
}
