package com.nomadconnection.dapp.core.domain.repository.limit;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LimitRecalculationRepository extends JpaRepository<LimitRecalculation, Long> {

	List<LimitRecalculation> findByCorp(Corp corp);

	Optional<LimitRecalculation> findByCorpAndDate(Corp corp, LocalDate date);
}
