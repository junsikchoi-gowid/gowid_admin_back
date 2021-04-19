package com.nomadconnection.dapp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialConsumersRequestDto {

    private Boolean overFiveEmployees;

}
