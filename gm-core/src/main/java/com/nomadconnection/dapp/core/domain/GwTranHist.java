package com.nomadconnection.dapp.core.domain;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class GwTranHist extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

//    @Column(columnDefinition = "varchar(9) COMMENT 'Transaction Code'")
//    private String c001; // Transaction Code

    @Column(columnDefinition = "varchar(3) COMMENT 'TEXT 개시문자'")
    private String c002; // TEXT 개시문자

    @Column(columnDefinition = "varchar(4) COMMENT '전문길이'")
    private String c003;  // 전문길이

    @Column(columnDefinition = "varchar(4) COMMENT '전문종별코드'")
    private String c004;  // 전문종별코드

    @Column(columnDefinition = "varchar(1) COMMENT '송수신 flag'")
    private String c005;  // 송수신 flag

    @Column(columnDefinition = "varchar(12) COMMENT '거래고유번호(Globally Unique Identifier)'")
    private String c006;  // 거래고유번호(Globally Unique Identifier)

    @Column(columnDefinition = "varchar(8) COMMENT '전문전송일자'")
    private String c007;  // 전문전송일자

    @Column(columnDefinition = "varchar(6) COMMENT '전문전송시각'")
    private String c008;  // 전문전송시각

    @Column(columnDefinition = "varchar(2) COMMENT '응답코드'")
    private String c009;  // 응답코드

//    @Column(columnDefinition = "varchar(12) COMMENT '회원사번호'")
//    private String c010;  // 회원사번호
//
//    @Column(columnDefinition = "varchar(12) COMMENT '대외기관코드'")
//    private String c011;  // 대외기관코드
//
//    @Column(columnDefinition = "varchar(12) COMMENT '조회제휴사번호'")
//    private String c012;  // 조회제휴사번호

    @Column(columnDefinition = "varchar(100) COMMENT '응답메시지'")
    private String c013;  // 응답메시지

}