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
public class D1520 extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private String c007;
    @Column(columnDefinition = "varchar(10) comment DEFAULT '' '사업자등록번호'")
    private String d001; //사업자등록번호
    @Column(columnDefinition = "varchar(14) comment DEFAULT '' '발급승인번호'")
    private String d002; //발급승인번호
    @Column(columnDefinition = "varchar(13) comment DEFAULT '' '주민번호'")
    private String d003; //주민번호
    @Column(columnDefinition = "varchar(80) comment DEFAULT '' '상호사업장명'")
    private String d004; //상호사업장명
    @Column(columnDefinition = "varchar(1) comment DEFAULT '' '발급가능여부'")
    private String d005; //발급가능여부
    @Column(columnDefinition = "varchar(8) comment DEFAULT '' '시작일자'")
    private String d006; //시작일자
    @Column(columnDefinition = "varchar(8) comment DEFAULT '' '종료일자'")
    private String d007; //종료일자
    @Column(columnDefinition = "varchar(50) comment DEFAULT '' '성명'")
    private String d008; //성명
    @Column(columnDefinition = "varchar(200) comment DEFAULT '' '주소'")
    private String d009; //주소
    @Column(columnDefinition = "varchar(100) comment DEFAULT '' '종목'")
    private String d010; //종목
    @Column(columnDefinition = "varchar(100) comment DEFAULT '' '업태'")
    private String d011; //업태
    @Column(columnDefinition = "varchar(8) comment DEFAULT '' '작성일자'")
    private String d012; //작성일자
    @Column(columnDefinition = "varchar(4) comment DEFAULT '' '귀속연도'")
    private String d013; //귀속연도
    @Column(columnDefinition = "varchar(40) comment DEFAULT '' '총자산'")
    private String d014; //총자산
    @Column(columnDefinition = "varchar(15) comment DEFAULT '' '매출'")
    private String d015; //매출
    @Column(columnDefinition = "varchar(15) comment DEFAULT '' '납입자본금'")
    private String d016; //납입자본금
    @Column(columnDefinition = "varchar(15) comment DEFAULT '' '자기자본금'")
    private String d017; //자기자본금
    @Column(columnDefinition = "varchar(15) comment DEFAULT '' '재무조사일'")
    private String d018; //재무조사일

}
