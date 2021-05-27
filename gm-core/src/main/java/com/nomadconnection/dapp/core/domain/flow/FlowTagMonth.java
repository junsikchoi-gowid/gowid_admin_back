package com.nomadconnection.dapp.core.domain.flow;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Accessors(fluent = true)
@Entity
public class FlowTagMonth extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_Corp_FlowTagMonth"),
            columnDefinition = "bigint(20) COMMENT '법인 idx'", nullable = false)
    private Corp corp;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idxFlowTag", foreignKey = @ForeignKey(name = "FK_FlowTagConfig_FlowTagMonth"),
            columnDefinition = "bigint(20) COMMENT 'FlowTagConfig idx'", nullable = false)
    private FlowTagConfig flowTagConfig;

    @Column(nullable = false, updatable = false, columnDefinition = "varchar(6) default '999912' comment 'Flow 설정일'")
    private String flowDate;

    @Column(columnDefinition = "DOUBLE DEFAULT NULL COMMENT '총액'")
    private Double flowTotal;


    public static FlowTagMonth of(FlowTagMonth dto) {
        return FlowTagMonth.builder()
                .corp(dto.corp())
                .flowTagConfig(dto.flowTagConfig())
                .flowDate(dto.flowDate())
                .flowTotal(dto.flowTotal())
                .build();
    }
}
