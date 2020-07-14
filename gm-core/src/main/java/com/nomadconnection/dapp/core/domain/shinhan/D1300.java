package com.nomadconnection.dapp.core.domain.shinhan;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
public class D1300 extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private String c007;

    @Column(nullable = false)
    private Long idxCorp;

    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '조회업종코드명'")
    private String d001; // 조회업종코드명
    @Column(columnDefinition = "varchar(6)    DEFAULT '' COMMENT '업종코드'")
    private String d002; // 업종코드
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '업종코드명'")
    private String d003; // 업종코드명

}
