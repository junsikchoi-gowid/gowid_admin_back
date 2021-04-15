package com.nomadconnection.dapp.core.domain.shinhan;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
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
public class D1400 extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idx;

    @Column(nullable = false)
    private String c007;
    @Column(nullable = false)
    private Long idxCorp;

    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '거래구분코드'")
    private String d001; // 거래구분코드
    @Column(columnDefinition = "varchar(10)    DEFAULT '' COMMENT '사업자등록번호'")
    private String d002; // 사업자등록번호
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '회원구분코드'")
    private String d003; // 회원구분코드
    @Column(columnDefinition = "varchar(80)    DEFAULT '' COMMENT '법인명'")
    private String d004; // 법인명
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '기업규모코드'")
    private String d005; // 기업규모코드

    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '대표자주민등록번호'")
    private String d006; // 대표자주민등록번호. 전문규격변경으로인해 더미필드 처리 (null세팅)
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자명'")
    private String d007; // 대표자명. 전문규격변경으로인해 더미필드 처리 (null세팅)

    @Column(columnDefinition = "varchar(13)    DEFAULT '' COMMENT '보증인고객식별번호'")
    private String d008; // 보증인고객식별번호
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '보증인고객한글명'")
    private String d009; // 보증인고객한글명
    @Column(columnDefinition = "varchar(15)    DEFAULT '' COMMENT '현재제휴한도'")
    private String d010; // 현재제휴한도
    @Column(columnDefinition = "varchar(6)    DEFAULT '' COMMENT '업종코드'")
    private String d011; // 업종코드
    @Column(columnDefinition = "varchar(6)    DEFAULT '' COMMENT '특화카드구분코드'")
    private String d012; // 특화카드구분코드
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '조건변경법인심사신청구분코드'")
    private String d013; // 조건변경법인심사신청구분코드
    @Column(columnDefinition = "varchar(15)    DEFAULT '' COMMENT '변경후제휴한도금액'")
    private String d014; // 변경후제휴한도금액
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '신청인사번'")
    private String d015; // 신청인사번
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '확인자사번'")
    private String d016; // 확인자사번
    @Column(columnDefinition = "varchar(200)    DEFAULT '' COMMENT '의견내용'")
    private String d017; // 의견내용
    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '등록지점코드'")
    private String d018; // 등록지점코드
    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '법인실소유자한글명'")
    private String d019; // 법인실소유자한글명
    @Column(columnDefinition = "varchar(80)    DEFAULT '' COMMENT '법인실소유자영문명'")
    private String d020; // 법인실소유자영문명
    @Column(columnDefinition = "varchar(6)    DEFAULT '' COMMENT '법인실소유자생년월일'")
    private String d021; // 법인실소유자생년월일
    @Column(columnDefinition = "varchar(2)    DEFAULT '' COMMENT '법인실소유자국적코드'")
    private String d022; // 법인실소유자국적코드
    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '법인실소유자유형코드'")
    private String d023; // 법인실소유자유형코드
    @Column(columnDefinition = "varchar(5)    DEFAULT '' COMMENT '자금세탁방지실소유자지분율'")
    private String d024; // 자금세탁방지실소유자지분율
    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '접수일자'")
    private String d025;  // 접수일자
    @Column(columnDefinition = "varchar(5)    DEFAULT '' COMMENT '접수순번'")
    private String d026;  // 접수순번


    @Column(columnDefinition = "varchar(13)    DEFAULT '' COMMENT '법인등록번호'")
    private String d027;    // 법인등록번호

    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '법인자격코드'")
    private String d028;    // 법인자격코드

    @Column(columnDefinition = "varchar(80)    DEFAULT '' COMMENT '법인영문명'")
    private String d029;    // 법인영문명

    @Column(columnDefinition = "varchar(8)    DEFAULT '' COMMENT '설립일자'")
    private String d030;    // 설립일자

    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '대표자코드'")
    private String d031;    // 대표자코드

    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자명1'")
    private String d032;    // 대표자명1

    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '대표자주민등록번호1'")
    private String d033;    // 대표자주민등록번호1

    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자영문명1'")
    private String d034;    // 대표자영문명1

    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '대표자국적코드1'")
    private String d035;    // 대표자국적코드1

    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자명2'")
    private String d036;    // 대표자명2

    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '대표자주민등록번호2'")
    private String d037;    // 대표자주민등록번호2

    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자영문명2'")
    private String d038;    // 대표자영문명2

    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '대표자국적코드2'")
    private String d039;    // 대표자국적코드2

    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자명3'")
    private String d040;    // 대표자명3

    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '대표자주민등록번호3'")
    private String d041;    // 대표자주민등록번호3

    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '대표자영문명3'")
    private String d042;    // 대표자영문명3

    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '대표자국적코드3'")
    private String d043;    // 대표자국적코드3

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '직장우편앞번호'")
    private String d044;    // 직장우편앞번호

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '직장우편뒷번호'")
    private String d045;    // 직장우편뒷번호

    @Column(columnDefinition = "varchar(100)    DEFAULT '' COMMENT '직장기본주소'")
    private String d046;    // 직장기본주소

    @Column(columnDefinition = "varchar(100)    DEFAULT '' COMMENT '직장상세주소'")
    private String d047;    // 직장상세주소

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '직장전화지역번호'")
    private String d048;    // 직장전화지역번호

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '직장전화국번호'")
    private String d049;    // 직장전화국번호

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '직장전화고유번호'")
    private String d050;    // 직장전화고유번호

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '팩스전화지역번호'")
    private String d051;    // 팩스전화지역번호

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '팩스전화국번호'")
    private String d052;    // 팩스전화국번호

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '팩스전화고유번호'")
    private String d053;    // 팩스전화고유번호

    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '신청관리자부서명'")
    private String d054;    // 신청관리자부서명

    @Column(columnDefinition = "varchar(20)    DEFAULT '' COMMENT '신청관리자직위명'")
    private String d055;    // 신청관리자직위명

    @Column(columnDefinition = "varchar(32)    DEFAULT '' COMMENT '신청관리자주민등록번호'")
    private String d056;    // 신청관리자주민등록번호

    @Column(columnDefinition = "varchar(50)    DEFAULT '' COMMENT '신청관리자명'")
    private String d057;    // 신청관리자명

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '신청관리자전화지역번호'")
    private String d058;    // 신청관리자전화지역번호

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '신청관리자전화국번호'")
    private String d059;    // 신청관리자전화국번호

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '신청관리자전화고유번호'")
    private String d060;    // 신청관리자전화고유번호

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '신청관리자전화내선번호'")
    private String d061;    // 신청관리자전화내선번호

    @Column(columnDefinition = "varchar(3)    DEFAULT '' COMMENT '신청관리자휴대전화식별번호'")
    private String d062;    // 신청관리자휴대전화식별번호

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '신청관리자휴대전화국번호'")
    private String d063;    // 신청관리자휴대전화국번호

    @Column(columnDefinition = "varchar(4)    DEFAULT '' COMMENT '신청관리자휴대전화고유번호'")
    private String d064;    // 신청관리자휴대전화고유번호

    @Column(columnDefinition = "varchar(60)    DEFAULT '' COMMENT '신청관리자이메일주소'")
    private String d065;    // 신청관리자이메일주소

    @Column(columnDefinition = "varchar(25)    DEFAULT '' COMMENT '도로명참조KEY값'")
    private String d066;    // 도로명참조KEY값

    @Column(columnDefinition = "varchar(6)    DEFAULT '' COMMENT '법인카드신청구분코드'")
    private String d067; //법인카드신청구분코드

    @Column(columnDefinition = "varchar(1)    DEFAULT '' COMMENT '금융소비자정보유형코드'")
    private String d068; //금융소비자정보유형코드

    @ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="idxCardIssuanceInfo", foreignKey = @ForeignKey(name = "FK_D1400_CardIssuanceInfo"), referencedColumnName = "idx", columnDefinition = "bigint(20) DEFAULT NULL COMMENT 'CardIssuanceInfo 식별값'")
    private CardIssuanceInfo cardIssuanceInfo;

    public void cleanUpOtherCeoInfo(){
        String emptyString = "";

        this.setD036(emptyString);
        this.setD037(emptyString);
        this.setD038(emptyString);
        this.setD039(emptyString);
        this.setD040(emptyString);
        this.setD041(emptyString);
        this.setD042(emptyString);
        this.setD043(emptyString);
    }
}
