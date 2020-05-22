package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Alarm;
import com.nomadconnection.dapp.core.domain.CommonCode;
import com.nomadconnection.dapp.core.domain.CommonCodeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, Long>{

}
