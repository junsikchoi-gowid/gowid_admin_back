package com.nomadconnection.dapp.core.domain.repository.lotte;

import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1000;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Lotte_D1000Repository extends JpaRepository<Lotte_D1000, Long> {
	Lotte_D1000 getTopByIdxCorpOrderByIdxDesc(Long idxCorp);
}
