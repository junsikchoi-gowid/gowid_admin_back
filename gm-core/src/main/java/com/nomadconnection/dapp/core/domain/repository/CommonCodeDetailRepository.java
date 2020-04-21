package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.CommonCode;
import com.nomadconnection.dapp.core.domain.CommonCodeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonCodeDetailRepository extends JpaRepository<CommonCodeDetail, Long>{

}
