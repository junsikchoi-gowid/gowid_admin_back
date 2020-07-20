package com.nomadconnection.dapp.core.domain.repository.common;

import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommonCodeDetailRepository extends JpaRepository<CommonCodeDetail, Long> {
    List<CommonCodeDetail> findAllByCode(CommonCodeType code);

    CommonCodeDetail getByCodeAndCode1(CommonCodeType code, String code1);

    CommonCodeDetail getByCode1AndCode5(String code1, String code5);

    CommonCodeDetail findFirstByCodeAndValue1(CommonCodeType code, String value1);

    Optional<CommonCodeDetail> findFirstByCodeAndValue1OrValue2(CommonCodeType code, String value1, String value2);
}
