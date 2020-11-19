package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeDto {

    @ApiModelProperty("코드")
    public CommonCodeType code;

    @ApiModelProperty("코드설명")
    public String desc;

}
