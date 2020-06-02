package com.nomadconnection.dapp.api.dto.shinhan.ui;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuanceDto {

    @ApiModelProperty("사업자등록번호")
    @NotEmpty
    private String businessLicenseNo;

    @ApiModelProperty("법인회원구분코드. 01: 신용카드회원")
    @NotEmpty
    private String memberTypeCode;      // 01: 신용카드회원
}
