package com.nomadconnection.dapp.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentDto {
    @ApiModelProperty("이용약관(식별자)")
    public Integer idxConsent;

    @ApiModelProperty("버전")
    public String version;

    @ApiModelProperty("제목")
    public String title;

    @ApiModelProperty("내용")
    public String contents;

    @ApiModelProperty("필수여부")
    public Boolean essential;

    @ApiModelProperty("현재사용여부")
    public Boolean enabled;
}
