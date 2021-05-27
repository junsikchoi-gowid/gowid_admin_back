package com.nomadconnection.dapp.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialConsumersResponseDto {

    private Long cardIssuanceInfoIdx;

    private Boolean overFiveEmployees;

    public static FinancialConsumersResponseDto from(CardIssuanceInfo cardIssuanceInfo){
        return FinancialConsumersResponseDto
            .builder()
            .cardIssuanceInfoIdx(cardIssuanceInfo.idx())
            .overFiveEmployees(cardIssuanceInfo.getFinancialConsumers().getOverFiveEmployees())
            .build();
    }

}
