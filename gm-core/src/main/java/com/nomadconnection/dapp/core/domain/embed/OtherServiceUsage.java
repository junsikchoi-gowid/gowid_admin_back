package com.nomadconnection.dapp.core.domain.embed;

import lombok.*;

import javax.persistence.Column;
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
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)  DEFAULT 'SETUP_COMPANY' COMMENT '지출관리 상태'")
    private ExpenseStatus expenseStatus;

    @Builder.Default
    private Boolean saasUsage = false; // SaaS 사용여부
}
