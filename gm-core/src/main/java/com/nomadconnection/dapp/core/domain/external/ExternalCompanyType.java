package com.nomadconnection.dapp.core.domain.external;

import lombok.Getter;

import java.util.Arrays;

/**
 * 외부 연계 업체 코드
 */
@Getter
public enum ExternalCompanyType {
    QUOTABOOK;

    public static ExternalCompanyType getType(ExternalCompanyType paramType) {
        return Arrays.stream(ExternalCompanyType.values())
                .filter(companyType -> companyType.equals(paramType))
                .findFirst()
                .orElse(null);
    }

}
