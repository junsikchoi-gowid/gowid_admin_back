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
public class StakeholdersRes {

    @ApiModelProperty("총 주식 수")
    @JsonProperty("num_shares_total")
    private Long numSharesTotal;

    @ApiModelProperty("주식 보유 목록")
    private List<Stakeholder> stakeholders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stakeholder {
        @ApiModelProperty("주주명")
        @JsonProperty("stakeholder_name")
        private String stakeholderName;

        @ApiModelProperty("소유권")
        private String ownership;

        @ApiModelProperty("보유 주식 수")
        @JsonProperty("num_shares")
        private Long numShares;
    }
}
