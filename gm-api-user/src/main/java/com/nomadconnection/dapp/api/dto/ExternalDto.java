package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.Risk;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalDto {
    @ApiModelProperty("이메일(계정)")
    private String email;

    @ApiModelProperty("비밀번호")
    private String password;

    @ApiModelProperty("사업자번호")
    private String idNo;

    @ApiModelProperty("검색기간 from ")
    private String from;

    @ApiModelProperty("검색기간 to ")
    private String to;
}
