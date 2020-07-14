package com.nomadconnection.dapp.core.domain.repository.common;

import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonCodeDetailRepository extends JpaRepository<CommonCodeDetail, Long> {
    List<CommonCodeDetail> findAllByCode(CommonCodeType codeType);

    CommonCodeDetail getByCodeAndCode1(CommonCodeType codeType, String code1);

    CommonCodeDetail getByCode1AndCode5(String code1, String code5);
}
