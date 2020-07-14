package com.nomadconnection.dapp.core.domain.repository.common;

import com.nomadconnection.dapp.core.domain.common.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, Long>{

}
