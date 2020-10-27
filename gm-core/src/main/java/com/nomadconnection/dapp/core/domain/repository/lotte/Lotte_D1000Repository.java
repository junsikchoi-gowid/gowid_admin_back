package com.nomadconnection.dapp.core.domain.repository.lotte;

import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1000;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface Lotte_D1000Repository extends JpaRepository<Lotte_D1000, Long> {
	Lotte_D1000 getTopByIdxCorpOrderByIdxDesc(Long idxCorp);

	@Transactional
	@Modifying
	@Query("delete from Lotte_D1000  where idxCorp = :idxCorp")
	void deleteByCorpIdx(@Param("idxCorp") Long idxCorp);
}
