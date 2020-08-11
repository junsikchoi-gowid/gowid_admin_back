package com.nomadconnection.dapp.core.domain.repository.common;

import com.nomadconnection.dapp.core.domain.common.IssuanceProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuanceProgressRepository extends JpaRepository<IssuanceProgress, Long> {

}
