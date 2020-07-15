package com.nomadconnection.dapp.core.domain.shinhan;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userIdx"}, name = "uk_userIdx"))
public class IssuanceProgress extends BaseTime {

    @Id
    @Column(nullable = false, updatable = false)
    private Long userIdx;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40) COMMENT 진행단계. NOT_SIGNED:서명전, SIGNED:서명 완료, RESUME:재개")
    private IssuanceProgressType progress;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40) COMMENT 상태. DEFAULT:초기상태(실행전), SUCCESS:성공, FAILED:실패")
    private IssuanceStatusType status;

}
