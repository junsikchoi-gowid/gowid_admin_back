package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.CommonCodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonCodeDetailRepository extends JpaRepository<CommonCodeDetail, Long> {
    List<CommonCodeDetail> findAllByCode(CommonCodeType codeType);

    CommonCodeDetail getByCodeAndCode1(CommonCodeType codeType, String code1);

    CommonCodeDetail getByCode1AndCode5(String code1, String code5);
}
