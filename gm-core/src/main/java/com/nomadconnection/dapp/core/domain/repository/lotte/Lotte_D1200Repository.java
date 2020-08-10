package com.nomadconnection.dapp.core.domain.repository.lotte;

import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1200;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Lotte_D1200Repository extends JpaRepository<Lotte_D1200, Long> {
	Lotte_D1200 getTopByIdxCorpOrderByIdxDesc(Long idxCorp);
}
