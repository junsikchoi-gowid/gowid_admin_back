package com.nomadconnection.dapp.api.service.expense.rpc.dto;

import com.nomadconnection.dapp.core.domain.embed.ExpenseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseStatusRes {
    private Integer corpId;
    private String name;
    private String registrationNumber;
    private ExpenseStatus setupStatus;
}
