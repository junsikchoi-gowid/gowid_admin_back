package com.nomadconnection.dapp.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class UserCorporationDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class registerCorporation {

        @ApiModelProperty("법인명(영문)")
        @NotEmpty
        private String engCorName;

        @ApiModelProperty("업종")
        @NotNull
        private Long businessType;

        @ApiModelProperty("사업장 전화번호")
        @NotEmpty
        private String corNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class registerVenture {

        @ApiModelProperty("벤처기업확인서 보유 여부")
        @NotNull
        private Boolean isVerifiedVenture;

        @ApiModelProperty("10억이상 VC투자 여부")
        @NotNull
        private Boolean isVC;

        @ApiModelProperty("투자사")
        private Long investor;

        @ApiModelProperty("누적투자금액")
        private String amount;
    }
}
