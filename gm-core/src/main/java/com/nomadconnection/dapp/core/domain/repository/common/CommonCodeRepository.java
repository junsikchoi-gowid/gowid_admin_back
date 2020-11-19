package com.nomadconnection.dapp.core.domain.repository.common;

import com.nomadconnection.dapp.core.domain.common.CommonCode;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, Long>{
	Optional<CommonCode> findAllByCode(CommonCodeType code);
}
