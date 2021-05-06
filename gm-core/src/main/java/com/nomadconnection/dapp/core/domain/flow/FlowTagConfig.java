package com.nomadconnection.dapp.core.domain.flow;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.dto.flow.FlowTagConfigDto;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Accessors(fluent = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"idxCorp","codeLv1","codeLv3","codeLv4"}, name = "UK_Corp_FlowTagConfig"))
@Entity
public class FlowTagConfig extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idxCorp", foreignKey = @ForeignKey(name = "FK_Corp_FlowTagConfig"),
            columnDefinition = "bigint(20) COMMENT '법인 idx'", nullable = false)
    private Corp corp;

    @Column(columnDefinition = "varchar(30) COMMENT 'flowCode'")
    private String flowCode;

    @Column(columnDefinition = "varchar(10) COMMENT 'code1'")
    private String code1;

    @Column(columnDefinition = "varchar(10) COMMENT 'code2'")
    private String code2;

    @Column(columnDefinition = "varchar(10) COMMENT 'code3'")
    private String code3;

    @Column(columnDefinition = "varchar(10) COMMENT 'code4'")
    private String code4;

    @Column(columnDefinition = "varchar(100) COMMENT '계정분류 1단계'")
    private String codeLv1;

    @Column(columnDefinition = "varchar(100) COMMENT '계정분류 2단계'")
    private String codeLv2;

    @Column(columnDefinition = "varchar(100) COMMENT '계정분류 3단계'")
    private String codeLv3;

    @Column(columnDefinition = "varchar(100) COMMENT '계정분류 4단계'")
    private String codeLv4;

    @Column(columnDefinition = "varchar(200) COMMENT '계정설명'")
    private String codeDesc;

    @Builder.Default
    @Column(columnDefinition = "bit(1) DEFAULT NULL COMMENT '사용유무'")
    private Boolean enabled = true;

    @Builder.Default
    @Column(columnDefinition = "bit(1) DEFAULT NULL COMMENT '삭제여부'")
    private Boolean deleteYn = false;

    @Column(columnDefinition = "smallint DEFAULT 0 COMMENT 'tag 순서'")
    private Integer tagOrder;

    public static FlowTagConfig of(FlowTagConfig dto, Corp corp) {
        return FlowTagConfig.builder()
                .corp(corp)
                .flowCode(dto.flowCode())
                .code1(dto.code1())
                .code2(dto.code2())
                .code3(dto.code3())
                .code4(dto.code4())
                .codeLv1(dto.codeLv1())
                .codeLv2(dto.codeLv2())
                .codeLv3(dto.codeLv3())
                .codeLv4(dto.codeLv4())
                .codeDesc(dto.codeDesc())
                .tagOrder(dto.tagOrder())
                .enabled(dto.enabled())
                .build();
    }


    public static FlowTagConfig delete(FlowTagConfig dto, Corp corp) {
        return FlowTagConfig.builder()
                .idx(dto.idx())
                .corp(corp)
                .flowCode(dto.flowCode())
                .code1(dto.code1())
                .code2(dto.code2())
                .code3(dto.code3())
                .code4(dto.code4())
                .codeLv1(dto.codeLv1())
                .codeLv2(dto.codeLv2())
                .codeLv3(dto.codeLv3())
                .codeLv4(dto.codeLv4())
                .codeDesc(dto.codeDesc())
                .tagOrder(dto.tagOrder())
                .enabled(dto.enabled())
                .deleteYn(true)
                .build();
    }
}
