package com.nomadconnection.dapp.core.domain;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class D1510 extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private String c007;

    @Column(nullable = false)
    private Long idxCorp;

    @Column(columnDefinition = "varchar(17)    DEFAULT '' COMMENT '발급번호'")
    private String d001; // 발급번호
    @Column(columnDefinition = "varchar(80)    DEFAULT '' COMMENT '법인명'")
    private String d002; // 법인명
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '사업자등록번호'")
    private String d003; // 사업자등록번호
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '사업자종류'")
    private String d004; // 사업자종류
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '성명'")
    private String d005; // 성명
    @Column(columnDefinition = "varchar(200)    DEFAULT '' COMMENT '사업장소재지'")
    private String d006; // 사업장소재지
    @Column(columnDefinition = "varchar(13)    DEFAULT '' COMMENT '주민등록번호'")
    private String d007; // 주민등록번호
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '개업일'")
    private String d008; // 개업일
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '사업자등록일'")
    private String d009; // 사업자등록일
    @Column(columnDefinition = "varchar(40)    DEFAULT '' COMMENT '발급기관'")
    private String d010; // 발급기관
    @Column(columnDefinition = "varchar(100)    DEFAULT '' COMMENT '업태'")
    private String d011; // 업태
    @Column(columnDefinition = "varchar(100)    DEFAULT '' COMMENT '종목'")
    private String d012; // 종목

}
