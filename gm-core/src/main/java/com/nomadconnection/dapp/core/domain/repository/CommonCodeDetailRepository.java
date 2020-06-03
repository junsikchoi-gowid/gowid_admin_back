package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.CommonCodeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonCodeDetailRepository extends JpaRepository<CommonCodeDetail, Long>{
	List<CommonCodeDetail> findAllByCode(String code);
}
