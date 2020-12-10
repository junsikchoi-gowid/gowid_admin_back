package com.nomadconnection.dapp.api.dto.limitInquiry;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitInquiryDto {

    @ApiModelProperty("희망한도")
    @NotNull(message = "hopeLimit is mandatory")
    private Long hopeLimit;

    @ApiModelProperty("연락처")
    @NotEmpty(message = "contact is mandatory")
    private String contact;

    @ApiModelProperty("법인명")
    @NotEmpty(message = "corporationName is mandatory")
    private String corporationName;

    @ApiModelProperty("메시지")
    private String content;
}
