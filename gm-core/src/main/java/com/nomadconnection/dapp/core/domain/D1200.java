package com.nomadconnection.dapp.core.domain;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class D1200 extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private String c007;

    @Column(nullable = false)
    private Long idxCorp;

    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '사업자등록번호'")
    private String d001;    //사업자등록번호
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '법인회원구분코드'")
    private String d002;    //법인회원구분코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '신규대상여부'")
    private String d003;    //신규대상여부
    @Column(columnDefinition = "varchar(15)    DEFAULT '' COMMENT '총한도금액'")
    private String d004;    //총한도금액
    @Column(columnDefinition = "varchar(15)    DEFAULT '' COMMENT '특화한도금액'")
    private String d005;    //특화한도금액
    @Column(columnDefinition = "varchar(15)    DEFAULT '' COMMENT '제휴약정한도금액'")
    private String d006;    //제휴약정한도금액

    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '신청접수일자'")
    private String d007;    //특화한도금액

    @Column(columnDefinition = "varchar(5)    DEFAULT '' COMMENT '신청접수순번'")
    private String d008;    //제휴약정한도금액

}
