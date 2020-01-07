package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.Consent;
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

    public static BrandConsentDto from (Consent consent){
        return BrandConsentDto.builder()
                .idx(consent.idx())
                .contents(consent.contents())
                .enabled(consent.enabled())
                .essential(consent.essential())
                .title(consent.title())
                .version(consent.version())
                .build();
    }
}
