package com.nomadconnection.dapp.core.domain.repository.lotte;

import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1200;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface Lotte_D1200Repository extends JpaRepository<Lotte_D1200, Long> {
	Lotte_D1200 getTopByIdxCorpOrderByIdxDesc(Long idxCorp);

	@Transactional
	@Modifying
	@Query("delete from Lotte_D1200  where idxCorp = :idxCorp")
	void deleteByCorpIdx(@Param("idxCorp") Long idxCorp);
}
