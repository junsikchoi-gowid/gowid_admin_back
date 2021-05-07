package com.nomadconnection.dapp.core.domain.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResAccountTypeStatus {
    DepositTrust("예금/신탁"),
    Loan("대출"),
    ForeignCurrency("외화"),
    Fund("펀드"),
    Stock("증권");

    private String status;
}