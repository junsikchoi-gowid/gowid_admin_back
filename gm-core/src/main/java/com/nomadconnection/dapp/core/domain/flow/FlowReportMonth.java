package com.nomadconnection.dapp.core.domain.flow;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import io.swagger.annotations.OAuth2Definition;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Accessors(fluent = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"idxCorp","flowDate"}, name = "UK_Corp_FlowDate"))
@Entity
public class FlowReportMonth extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_Corp_FlowReportMonth"), columnDefinition = "bigint(20) COMMENT '법인 idx'", nullable = false)
    private Corp corp;

    @Column(nullable = false, updatable = false, columnDefinition = "varchar(6) default '999912' comment 'Flow 설정일'")
    private String flowDate;

    @Column(columnDefinition = "DOUBLE DEFAULT NULL COMMENT '입금'")
    private Double flowIn;

    @Column(columnDefinition = "DOUBLE DEFAULT NULL COMMENT '출금'")
    private Double flowOut;

    @Column(columnDefinition = "DOUBLE DEFAULT NULL COMMENT '총액'")
    private Double flowTotal;

    public static FlowReportMonth of(FlowReportMonth dto){
        return FlowReportMonth.builder()
                .flowIn(dto.flowIn)
                .flowOut(dto.flowOut)
                .flowTotal(dto.flowTotal)
                .build();
    }
}
