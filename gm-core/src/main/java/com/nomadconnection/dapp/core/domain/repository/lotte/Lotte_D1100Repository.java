package com.nomadconnection.dapp.core.domain.repository.lotte;

import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1100;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Lotte_D1100Repository extends JpaRepository<Lotte_D1100, Long> {
	Lotte_D1100 getTopByIdxCorpOrderByIdxDesc(Long idxCorp);
}
