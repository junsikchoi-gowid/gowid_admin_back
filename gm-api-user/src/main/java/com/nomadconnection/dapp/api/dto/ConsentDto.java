package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.CardCompany;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentDto {
    @ApiModelProperty("이용약관(식별자)")
    public Long idxConsent;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegDto{
        @ApiModelProperty("이용약관(식별자)")
        public Long idxConsent;

        @ApiModelProperty("체크여부")
        public boolean status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterUserConsent {
        @ApiModelProperty("회사코드")
        private CardCompany companyCode;

        @ApiModelProperty("이용약관 정보")
        private List<RegDto> consents;
    }
}
