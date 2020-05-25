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
public class D1400 extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private String c007;
    @Column(columnDefinition = "varchar(1) comment DEFAULT '' '거래구분코드'")
    private String d001; // 거래구분코드
    @Column(columnDefinition = "varchar(10) comment DEFAULT '' '사업자등록번호'")
    private String d002; // 사업자등록번호
    @Column(columnDefinition = "varchar(2) comment DEFAULT '' '회원구분코드'")
    private String d003; // 회원구분코드
    @Column(columnDefinition = "varchar(80) comment DEFAULT '' '법인명'")
    private String d004; // 법인명
    @Column(columnDefinition = "varchar(2) comment DEFAULT '' '기업규모코드'")
    private String d005; // 기업규모코드
    @Column(columnDefinition = "varchar(13) comment DEFAULT '' '대표자주민등록번호'")
    private String d006; // 대표자주민등록번호
    @Column(columnDefinition = "varchar(50) comment DEFAULT '' '대표자명'")
    private String d007; // 대표자명
    @Column(columnDefinition = "varchar(13) comment DEFAULT '' '보증인고객식별번호'")
    private String d008; // 보증인고객식별번호
    @Column(columnDefinition = "varchar(50) comment DEFAULT '' '보증인고객한글명'")
    private String d009; // 보증인고객한글명
    @Column(columnDefinition = "varchar(15) comment DEFAULT '' '현재제휴한도'")
    private String d010; // 현재제휴한도
    @Column(columnDefinition = "varchar(6) comment DEFAULT '' '업종코드'")
    private String d011; // 업종코드
    @Column(columnDefinition = "varchar(6) comment DEFAULT '' '특화카드구분코드'")
    private String d012; // 특화카드구분코드
    @Column(columnDefinition = "varchar(2) comment DEFAULT '' '조건변경법인심사신청구분코드'")
    private String d013; // 조건변경법인심사신청구분코드
    @Column(columnDefinition = "varchar(15) comment DEFAULT '' '변경후제휴한도금액'")
    private String d014; // 변경후제휴한도금액
    @Column(columnDefinition = "varchar(8) comment DEFAULT '' '신청인사번'")
    private String d015; // 신청인사번
    @Column(columnDefinition = "varchar(8) comment DEFAULT '' '확인자사번'")
    private String d016; // 확인자사번
    @Column(columnDefinition = "varchar(200) comment DEFAULT '' '의견내용'")
    private String d017; // 의견내용
    @Column(columnDefinition = "varchar(4) comment DEFAULT '' '등록지점코드'")
    private String d018; // 등록지점코드
    @Column(columnDefinition = "varchar(50) comment DEFAULT '' '법인실소유자한글명'")
    private String d019; // 법인실소유자한글명
    @Column(columnDefinition = "varchar(80) comment DEFAULT '' '법인실소유자영문명'")
    private String d020; // 법인실소유자영문명
    @Column(columnDefinition = "varchar(6) comment DEFAULT '' '법인실소유자생년월일'")
    private String d021; // 법인실소유자생년월일
    @Column(columnDefinition = "varchar(2) comment DEFAULT '' '법인실소유자국적코드'")
    private String d022; // 법인실소유자국적코드
    @Column(columnDefinition = "varchar(1) comment DEFAULT '' '법인실소유자유형코드'")
    private String d023; // 법인실소유자유형코드
    @Column(columnDefinition = "varchar(5) comment DEFAULT '' '자금세탁방지실소유자지분율'")
    private String d024; // 자금세탁방지실소유자지분율

}
