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
public class RoundingRes {

    @ApiModelProperty("총 펀딩 수")
    @JsonProperty("num_rounds_total")
    private Long numRoundsTotal;

    @ApiModelProperty("펀딩 내역")
    private List<Round> rounds;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Round {
        @ApiModelProperty("펀드명")
        @JsonProperty("round_name")
        private String roundName;

        @ApiModelProperty("자본 출자")
        @JsonProperty("capital_contribution")
        private String capitalContribution;

        @ApiModelProperty("평가금액")
        @JsonProperty("valuation")
        private String valuation;

        @ApiModelProperty("날짜")
        @JsonProperty("date")
        private String date;
    }
}
