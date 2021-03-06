package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.consent.Consent;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandConsentDto {
    @ApiModelProperty("이용약관(식별자)")
    public Long idx;

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

    @ApiModelProperty("이용약관 순서")
    public Long consentOrder;

    @ApiModelProperty("이용약관 Type 법인 ")
    public boolean corpStatus;

    @ApiModelProperty("이용약관 Type ")
    public String typeCode;


    public static BrandConsentDto from(Consent consent) {
        return BrandConsentDto.builder()
                .idx(consent.idx())
                .contents(consent.contents())
                .enabled(consent.enabled())
                .essential(consent.essential())
                .title(consent.title())
                .version(consent.version())
                .consentOrder(consent.consentOrder())
                .typeCode(consent.typeCode())
                .build();
    }
}

