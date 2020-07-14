package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.etc.Faq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandFaqDto {
    @ApiModelProperty("이용약관(식별자)")
    public Long idx;

    @ApiModelProperty("제목")
    public String title;

    @ApiModelProperty("내용")
    public String contents;

    @ApiModelProperty("메일")
    public String email;

    @ApiModelProperty("답변여부")
    public Boolean replyStatus;

    public static BrandFaqDto from(Faq faq) {
        return BrandFaqDto.builder()
                .idx(faq.idx())
                .title(faq.title())
                .contents(faq.contents())
                .email(faq.email())
                .replyStatus(faq.replyStatus())
                .build();
    }
}
