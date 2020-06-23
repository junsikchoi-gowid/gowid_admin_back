package com.nomadconnection.dapp.core.domain;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
public class D1100 extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private String c007;

    @Column(nullable = false)
    private Long idxCorp;

    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '법인고객번호'")
    private String d001; //법인고객번호
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '법인회원구분코드'")
    private String d002; //법인회원구분코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '중복발급구분코드'")
    private String d003; //중복발급구분코드
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '사업장번호'")
    private String d004; //사업장번호
    @Column(columnDefinition = "varchar(6)    DEFAULT '' COMMENT '카드상품번호'")
    private String d005; //카드상품번호
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '카드등급코드'")
    private String d006; //카드등급코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '카드브랜드코드'")
    private String d007; //카드브랜드코드
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '카드디자인구분코드'")
    private String d008; //카드디자인구분코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '카드외형구분코드'")
    private String d009; //카드외형구분코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '카드IC용도구분코드'")
    private String d010; //카드IC용도구분코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '카드사진구분코드'")
    private String d011; //카드사진구분코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '교통기능구분코드'")
    private String d012; //교통기능구분코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '카드유형코드'")
    private String d013; //카드유형코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '카드IC현금기능구분코드'")
    private String d014; //카드IC현금기능구분코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '현금기능부여여부'")
    private String d015; //현금기능부여여부
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '제휴정보명'")
    private String d016; //제휴정보명
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '법인사업장상품코드'")
    private String d017; //법인사업장상품코드
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '카드한도방식구분코드'")
    private String d018; //카드한도방식구분코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '카드한도사용여부'")
    private String d019; //카드한도사용여부
    @Column(columnDefinition = "varchar(15)    DEFAULT '' COMMENT '카드한도금액'")
    private String d020; //카드한도금액
    @Column(columnDefinition = "varchar(64)    DEFAULT '' COMMENT '비밀번호'")
    private String d021; //비밀번호
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '결제방법코드'")
    private String d022; //결제방법코드
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '결제일'")
    private String d023; //결제일
    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '은행코드'")
    private String d024; //은행코드
    @Column(columnDefinition = "varchar(24)    DEFAULT '' COMMENT '결제계좌'")
    private String d025; //결제계좌
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '예금주명'")
    private String d026; //예금주명
    @Column(columnDefinition = "varchar(13)    DEFAULT '' COMMENT '예금주주민번호'")
    private String d027; //예금주주민번호
    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '예금주관계코드'")
    private String d028; //예금주관계코드
    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '명세서수령방법코드'")
    private String d029; //명세서수령방법코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '법인카드수령인구분코드'")
    private String d030; //법인카드수령인구분코드
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '수령지우편앞번호'")
    private String d031; //수령지우편앞번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '수령지우편뒷번호'")
    private String d032; //수령지우편뒷번호
    @Column(columnDefinition = "varchar(100)    DEFAULT '' COMMENT '카드수령지기본주소'")
    private String d033; //카드수령지기본주소
    @Column(columnDefinition = "varchar(100)    DEFAULT '' COMMENT '카드수령지상세주소'")
    private String d034; //카드수령지상세주소
    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '수령인휴대전화식별번호'")
    private String d035; //수령인휴대전화식별번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '수령인휴대전화국번호'")
    private String d036; //수령인휴대전화국번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '수령인휴대전화고유번호'")
    private String d037; //수령인휴대전화고유번호
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT 'SMS신청여부'")
    private String d038; //SMS신청여부
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '공동카드매수'")
    private String d039; //공동카드매수
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '권유자사번'")
    private String d040; //권유자사번
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '본인확인자사번'")
    private String d041; //본인확인자사번
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '필수조회동의여부'")
    private String d042; //필수조회동의여부
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '필수제공동의여부'")
    private String d043; //필수제공동의여부
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '고객식별정보처리동의'")
    private String d044; //고객식별정보처리동의
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '필수수집이용동의여부'")
    private String d045; //필수수집이용동의여부
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '카드수령지주소종류코드'")
    private String d046; //카드수령지주소종류코드
    @Column(columnDefinition = "varchar(100)    DEFAULT '' COMMENT '카드수령지도로명참조주소'")
    private String d047; //카드수령지도로명참조주소
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '실물카드여부'")
    private String d048; //실물카드여부
    @Column(columnDefinition = "varchar(30)    DEFAULT '' COMMENT '제휴정보'")
    private String d049; //제휴정보
}
