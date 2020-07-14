package com.nomadconnection.dapp.core.domain.shinhan;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userIdx"}, name = "uk_userIdx"))
public class IssuanceProgress extends BaseTime {

    @Id
    @Column(nullable = false, updatable = false)
    private Long userIdx;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40) COMMENT '진행단계'")
    private IssuanceProgressType progress;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(40) COMMENT '상태'")
    private IssuanceStatusType status;

}
