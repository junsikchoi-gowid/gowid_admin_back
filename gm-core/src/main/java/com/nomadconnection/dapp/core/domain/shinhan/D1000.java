package com.nomadconnection.dapp.core.domain.shinhan;


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
public class D1000 extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private String c007;

    @Column(nullable = false)
    private Long idxCorp;

    @Column(columnDefinition = "varchar(10)  DEFAULT '' COMMENT   '사업자등록번호'")
    private String d001;    //사업자등록번호
    @Column(columnDefinition = "varchar(13)    DEFAULT '' COMMENT '법인등록번호'")
    private String d002;    //법인등록번호
    @Column(columnDefinition = "varchar(80)    DEFAULT '' COMMENT '법인명'")
    private String d003;    //법인명
    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '법인자격코드'")
    private String d004;    //법인자격코드
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '기업규모코드'")
    private String d005;    //기업규모코드
    @Column(columnDefinition = "varchar(80)    DEFAULT '' COMMENT '법인영문명'")
    private String d006;    //법인영문명
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '설립일자'")
    private String d007;    //설립일자
    @Column(columnDefinition = "varchar(6)    DEFAULT '' COMMENT '업종코드'")
    private String d008;    //업종코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '대표자코드'")
    private String d009;    //대표자코드
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자명1'")
    private String d010;    //대표자명1
    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '대표자주민등록번호1'")
    private String d011;    //대표자주민등록번호1
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자영문명1'")
    private String d012;    //대표자영문명1
    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '대표자국적코드1'")
    private String d013;    //대표자국적코드1
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자명2'")
    private String d014;    //대표자명2
    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '대표자주민등록번호2'")
    private String d015;    //대표자주민등록번호2
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자영문명2'")
    private String d016;    //대표자영문명2
    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '대표자국적코드2'")
    private String d017;    //대표자국적코드2
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자명3'")
    private String d018;    //대표자명3
    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '대표자주민등록번호3'")
    private String d019;    //대표자주민등록번호3
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자영문명3'")
    private String d020;    //대표자영문명3
    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '대표자국적코드3'")
    private String d021;    //대표자국적코드3
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '직장우편앞번호'")
    private String d022;    //직장우편앞번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '직장우편뒷번호'")
    private String d023;    //직장우편뒷번호
    @Column(columnDefinition = "varchar(100)    DEFAULT '' COMMENT '직장기본주소'")
    private String d024;    //직장기본주소
    @Column(columnDefinition = "varchar(100)    DEFAULT '' COMMENT '직장상세주소'")
    private String d025;    //직장상세주소
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '직장전화지역번호'")
    private String d026;    //직장전화지역번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '직장전화국번호'")
    private String d027;    //직장전화국번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '직장전화고유번호'")
    private String d028;    //직장전화고유번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '팩스전화지역번호'")
    private String d029;    //팩스전화지역번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '팩스전화국번호'")
    private String d030;    //팩스전화국번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '팩스전화고유번호'")
    private String d031;    //팩스전화고유번호
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '신청관리자부서명'")
    private String d032;    //신청관리자부서명
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '신청관리자직위명'")
    private String d033;    //신청관리자직위명
    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '신청관리자주민등록번호'")
    private String d034;    //신청관리자주민등록번호
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '신청관리자명'")
    private String d035;    //신청관리자명
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '신청관리자전화지역번호'")
    private String d036;    //신청관리자전화지역번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '신청관리자전화국번호'")
    private String d037;    //신청관리자전화국번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '신청관리자전화고유번호'")
    private String d038;    //신청관리자전화고유번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '신청관리자전화내선번호'")
    private String d039;    //신청관리자전화내선번호
    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '신청관리자휴대전화식별번호'")
    private String d040;    //신청관리자휴대전화식별번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '신청관리자휴대전화국번호'")
    private String d041;    //신청관리자휴대전화국번호
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '신청관리자휴대전화고유번호'")
    private String d042;    //신청관리자휴대전화고유번호
    @Column(columnDefinition = "varchar(60)    DEFAULT '' COMMENT '신청관리자이메일주소'")
    private String d043;    //신청관리자이메일주소
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '등록지점코드'")
    private String d044;    //등록지점코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '유효기간코드'")
    private String d045;    //유효기간코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '보증인등록여부'")
    private String d046;    //보증인등록여부
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '담보등록여부'")
    private String d047;    //담보등록여부
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '신청경로코드'")
    private String d048;    //신청경로코드
    @Column(columnDefinition = "varchar(6)    DEFAULT '' COMMENT '카드상품번호'")
    private String d049;    //카드상품번호
    @Column(columnDefinition = "varchar(15)    DEFAULT '' COMMENT '제휴약정한도금액'")
    private String d050;    //제휴약정한도금액
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '신청구분코드'")
    private String d051;    //신청구분코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '예외접수여부'")
    private String d052;    //예외접수여부
    @Column(columnDefinition = "varchar(200)    DEFAULT '' COMMENT '의견내용'")
    private String d053;    //의견내용
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '주소종류코드'")
    private String d054;    //주소종류코드
    @Column(columnDefinition = "varchar(25)    DEFAULT '' COMMENT '도로명참조KEY값'")
    private String d055;    //도로명참조KEY값
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '대표자정보체크여부1'")
    private String d056;    //대표자정보체크여부1
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '대표자정보체크여부2'")
    private String d057;    //대표자정보체크여부2
    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '법인기업형태코드'")
    private String d058;    //법인기업형태코드
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '법인실소유자한글명'")
    private String d059;    //법인실소유자한글명
    @Column(columnDefinition = "varchar(80)    DEFAULT '' COMMENT '법인실소유자영문명'")
    private String d060;    //법인실소유자영문명
    @Column(columnDefinition = "varchar(6)    DEFAULT '' COMMENT '법인실소유자생년월일'")
    private String d061;    //법인실소유자생년월일
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '법인실소유자국적코드'")
    private String d062;    //법인실소유자국적코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '법인실소유자제외사유코드'")
    private String d063;    //법인실소유자제외사유코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '법인실소유자유형코드'")
    private String d064;    //법인실소유자유형코드
    @Column(columnDefinition = "varchar(5)    DEFAULT '' COMMENT '자금세탁방지실소유자지분율'")
    private String d065;    //자금세탁방지실소유자지분율
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '외국인여부'")
    private String d066;    //외국인여부
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '법인신청대리인명'")
    private String d067;    //법인신청대리인명
    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '법인신청대리인주민등록번호'")
    private String d068;    //법인신청대리인주민등록번호
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '법인신청대리인부서명'")
    private String d069;    //법인신청대리인부서명
    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '법인신청대리인직위명'")
    private String d070;    //법인신청대리인직위명
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '접수일자'")
    private String d071;    //접수일자
    @Column(columnDefinition = "varchar(5)    DEFAULT '' COMMENT '접수순번'")
    private String d072;    //접수순번

    public void cleanUpOtherCeoInfo(){
        String emptyString = "";
        this.setD014(emptyString);
        this.setD015(emptyString);
        this.setD016(emptyString);
        this.setD017(emptyString);
        this.setD018(emptyString);
        this.setD019(emptyString);
        this.setD020(emptyString);
        this.setD021(emptyString);
    }



}
