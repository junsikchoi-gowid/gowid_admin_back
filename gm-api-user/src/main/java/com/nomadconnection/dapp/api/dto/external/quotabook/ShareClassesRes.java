package com.nomadconnection.dapp.api.dto.external.quotabook;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShareClassesRes {

    @ApiModelProperty("총 보유분")
    @JsonProperty("num_shares_total")
    private Long numSharesTotal;

    @ApiModelProperty("주식 보유 목록")
    @JsonProperty("share_classes")
    private List<ShareClass> shareClasses;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShareClass {

        @ApiModelProperty("주식 유형 명")
        @JsonProperty("share_class_name")
        private String shareClassName;

        @ApiModelProperty("주식 수")
        @JsonProperty("num_shares")
        private Long numShares;

        @ApiModelProperty("보유 퍼센트")
        @JsonProperty("percentage_shares")
        private String percentageShares;

        @ApiModelProperty("주주 수")
        @JsonProperty("num_stakeholder")
        private String numStakeholder;

    }
}
