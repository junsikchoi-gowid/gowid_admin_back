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
public class D1200 extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private String c007;
    @Column(columnDefinition = "varchar(10) comment DEFAULT '' '사업자등록번호'")
    private String d001;    //사업자등록번호
    @Column(columnDefinition = "varchar(2) comment DEFAULT '' '법인회원구분코드'")
    private String d002;    //법인회원구분코드
    @Column(columnDefinition = "varchar(1) comment DEFAULT '' '신규대상여부'")
    private String d003;    //신규대상여부
    @Column(columnDefinition = "varchar(15) comment DEFAULT '' '총한도금액'")
    private String d004;    //총한도금액
    @Column(columnDefinition = "varchar(15) comment DEFAULT '' '특화한도금액'")
    private String d005;    //특화한도금액
    @Column(columnDefinition = "varchar(15) comment DEFAULT '' '제휴약정한도금액'")
    private String d006;    //제휴약정한도금액
}
