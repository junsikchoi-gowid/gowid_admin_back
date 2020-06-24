package com.nomadconnection.dapp.core.domain;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
public class D1530 extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private String c007;

    @Column(nullable = false)
    private Long idxCorp;

    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '신청접수일자'")
    private String d001; //신청접수일자
    @Column(columnDefinition = "varchar(5)    DEFAULT '' COMMENT '신청접수순번'")
    private String d002; //신청접수순번
    @Column(columnDefinition = "varchar(40)    DEFAULT '' COMMENT '문서제목'")
    private String d003;    //문서제목
    @Column(columnDefinition = "varchar(6)    DEFAULT '' COMMENT '등기번호'")
    private String d004;    //등기번호
    @Column(columnDefinition = "varchar(13)    DEFAULT '' COMMENT '등록번호'")
    private String d005;    //등록번호
    @Column(columnDefinition = "varchar(40)    DEFAULT '' COMMENT '관할등기소'")
    private String d006;    //관할등기소
    @Column(columnDefinition = "varchar(40)    DEFAULT '' COMMENT '발행등기소'")
    private String d007;    //발행등기소
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '발행일자'")
    private String d008;    //발행일자
    @Column(columnDefinition = "varchar(80)    DEFAULT '' COMMENT '상호'")
    private String d009;    //상호
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '상호_변경일자'")
    private String d010;    //상호_변경일자
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '상호_등기일자'")
    private String d011;    //상호_등기일자
    @Column(columnDefinition = "varchar(200)    DEFAULT '' COMMENT '본점주소'")
    private String d012;    //본점주소
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '본점주소_변경일자'")
    private String d013;    //본점주소_변경일자
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '본점주소_등기일자'")
    private String d014;    //본점주소_등기일자
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '1주의금액'")
    private String d015;    //1주의금액
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '1주의금액_변경일자'")
    private String d016;    //1주의금액_변경일자
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '1주의금액_등기일자'")
    private String d017;    //1주의금액_등기일자
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '발행할주식의총수'")
    private String d018;    //발행할주식의총수
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '발행할주식의총수_변경일자'")
    private String d019;    //발행할주식의총수_변경일자
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '발행할주식의총수_등기일자'")
    private String d020;    //발행할주식의총수_등기일자
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '발행주식현황_총수'")
    private String d021;    //발행주식현황_총수
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '발행주식현황_종류1'")
    private String d022;    //발행주식현황_종류1
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '발행주식현황_종류1_수량'")
    private String d023;    //발행주식현황_종류1_수량
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '발행주식현황_종류2'")
    private String d024;    //발행주식현황_종류2
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '발행주식현황_종류2_수량'")
    private String d025;    //발행주식현황_종류2_수량
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '발행주식현황_종류3'")
    private String d026;    //발행주식현황_종류3
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '발행주식현황_종류3_수량'")
    private String d027;    //발행주식현황_종류3_수량
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '발행주식현황_종류4'")
    private String d028;    //발행주식현황_종류4
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '발행주식현황_종류4_수량'")
    private String d029;    //발행주식현황_종류4_수량
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '발행주식현황_종류5'")
    private String d030;    //발행주식현황_종류5
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '발행주식현황_종류5_수량'")
    private String d031;    //발행주식현황_종류5_수량
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '발행주식현황_종류6'")
    private String d032;    //발행주식현황_종류6
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '발행주식현황_종류6_수량'")
    private String d033;    //발행주식현황_종류6_수량
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '발행주식현황_종류7'")
    private String d034;    //발행주식현황_종류7
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '발행주식현황_종류7_수량'")
    private String d035;    //발행주식현황_종류7_수량
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '발행주식현황_종류8'")
    private String d036;    //발행주식현황_종류8
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '발행주식현황_종류8_수량'")
    private String d037;    //발행주식현황_종류8_수량
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '발행주식현황_종류9'")
    private String d038;    //발행주식현황_종류9
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '발행주식현황_종류9_수량'")
    private String d039;    //발행주식현황_종류9_수량
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '발행주식현황_종류10'")
    private String d040;    //발행주식현황_종류10
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '발행주식현황_종류10_수량'")
    private String d041;    //발행주식현황_종류10_수량
    @Column(columnDefinition = "varchar(15)    DEFAULT '' COMMENT '발행주식현황_자본금의액'")
    private String d042;    //발행주식현황_자본금의액
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '발행주식현황_변경일자'")
    private String d043;    //발행주식현황_변경일자
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '발행주식현황_등기일자'")
    private String d044;    //발행주식현황_등기일자
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '대표이사_직위1'")
    private String d045;    //대표이사_직위1
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '대표이사_성명1'")
    private String d046;    //대표이사_성명1
    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '대표이사_주민번호1'")
    private String d047;    //대표이사_주민번호1
    @Column(columnDefinition = "varchar(200)    DEFAULT '' COMMENT '대표이사_주소1'")
    private String d048;    //대표이사_주소1
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '대표이사_직위2'")
    private String d049;    //대표이사_직위2
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '대표이사_성명2'")
    private String d050;    //대표이사_성명2
    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '대표이사_주민번호2'")
    private String d051;    //대표이사_주민번호2
    @Column(columnDefinition = "varchar(200)    DEFAULT '' COMMENT '대표이사_주소2'")
    private String d052;    //대표이사_주소2
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '대표이사_직위3'")
    private String d053;    //대표이사_직위3
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '대표이사_성명3'")
    private String d054;    //대표이사_성명3
    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '대표이사_주민번호3'")
    private String d055;    //대표이사_주민번호3
    @Column(columnDefinition = "varchar(200)    DEFAULT '' COMMENT '대표이사_주소3'")
    private String d056;    //대표이사_주소3
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '법인성립연월일'")
    private String d057;    //법인성립연월일


}
