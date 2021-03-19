package com.nomadconnection.dapp.core.domain.embed;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtherServiceUsage {
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ExpenseStatus expenseStatus = ExpenseStatus.SETUP_COMPANY;

    @Builder.Default
    private Boolean saasUsage = false; // SaaS 사용여부
}
