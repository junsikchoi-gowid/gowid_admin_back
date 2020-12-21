package com.nomadconnection.dapp.api.dto.external.quotabook;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundingReq {

    @ApiModelProperty("현재 페이지")
    private Long current;

    @ApiModelProperty("페이지 사이즈")
    private Long pageSize;

}
