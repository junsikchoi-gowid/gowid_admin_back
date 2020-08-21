package com.nomadconnection.dapp.core.domain.lotte;

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
public class Lotte_D1100 extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Column(nullable = false)
	private Long idxCorp;

	@Column
	private String transferDate;

	@Builder.Default
	@Column(columnDefinition = "varchar(3)  DEFAULT '531' COMMENT   '유치경로코드'")
	private String arPhc = "531";

	@Builder.Default
	@Column(columnDefinition = "varchar(2)  DEFAULT '01' COMMENT   '카드신청구분코드'")
	private String cdaplDc = "01";

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '대표자유형'")
	private String dgTc;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '대표자주민번호'")
	private String dgRrno;

	@Column(columnDefinition = "varchar(96)  DEFAULT '' COMMENT   '고객명'")
	private String cstNm;

	@Column(columnDefinition = "varchar(64)  DEFAULT '' COMMENT   '고객영문명'")
	private String cstEnm;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '국적코드'")
	private String natyC;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '대표자주민번호2'")
	private String dgRrno2;

	@Column(columnDefinition = "varchar(96)  DEFAULT '' COMMENT   '고객명2'")
	private String cstNm2;

	@Column(columnDefinition = "varchar(64)  DEFAULT '' COMMENT   '고객영문명2'")
	private String cstEnm2;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '국적코드2'")
	private String natyC2;

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '남녀구분코드2'")
	private String maFemDc2;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '대표자주민번호3'")
	private String dgRrno3;

	@Column(columnDefinition = "varchar(96)  DEFAULT '' COMMENT   '고객명3'")
	private String cstNm3;

	@Column(columnDefinition = "varchar(64)  DEFAULT '' COMMENT   '고객영문명3'")
	private String cstEnm3;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '국적코드3'")
	private String natyC3;

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '남녀구분코드3'")
	private String maFemDc3;

	@Column(columnDefinition = "varchar(13)  DEFAULT '' COMMENT   '사업자번호'")
	private String bzno;

	@Column(columnDefinition = "varchar(13)  DEFAULT '' COMMENT   '법인번호'")
	private String cpNo;

	@Column(columnDefinition = "varchar(8)  DEFAULT '' COMMENT   '설립일자'")
	private String estbDt;

	@Builder.Default
	@Column(columnDefinition = "varchar(6)  DEFAULT 'Z00' COMMENT   '법인업종코드'")
	private String cpBtc = "Z00";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT 'L' COMMENT   '법인유형코드'")
	private String cpTc = "L";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT '5' COMMENT   '법인규모코드'")
	private String cpSclC = "5";

	@Builder.Default
	@Column(columnDefinition = "varchar(2)  DEFAULT '54' COMMENT   '법인규모상세코드'")
	private String cpSclDtc = "54";

	@Builder.Default
	@Column(columnDefinition = "varchar(2)  DEFAULT '01' COMMENT   '법인카드상품구분코드'")
	private String cocPdDc = "01";

	@Column(columnDefinition = "varchar(7)  DEFAULT '' COMMENT   '카드상품종류코드'")
	private String unitCdC;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '로고구분코드'")
	private String braDc;

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT '4' COMMENT   '카드구분코드'")
	private String cdDc = "4";

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '카드등급코드'")
	private String cdGdc;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '법인카드상품구분코드2'")
	private String cocPdDc2;

	@Column(columnDefinition = "varchar(7)  DEFAULT '' COMMENT   '카드상품종류코드2'")
	private String unitCdC2;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '로고구분코드2'")
	private String braDc2;

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '카드구분코드2'")
	private String cdDc2;

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '카드등급코드2'")
	private String cdGdc2;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '법인카드상품구분코드3'")
	private String cocPdDc3;

	@Column(columnDefinition = "varchar(7)  DEFAULT '' COMMENT   '카드상품종류코드3'")
	private String unitCdC3;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '로고구분코드3'")
	private String braDc3;

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '카드구분코드3'")
	private String cdDc3;

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '카드등급코드3'")
	private String cdGdc3;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '법인카드상품구분코드4'")
	private String cocPdDc4;

	@Column(columnDefinition = "varchar(7)  DEFAULT '' COMMENT   '카드상품종류코드4'")
	private String unitCdC4;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '로고구분코드4'")
	private String braDc4;

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '카드구분코드4'")
	private String cdDc4;

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '카드등급코드4'")
	private String cdGdc4;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '법인카드상품구분코드5'")
	private String cocPdDc5;

	@Column(columnDefinition = "varchar(7)  DEFAULT '' COMMENT   '카드상품종류코드5'")
	private String unitCdC5;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '로고구분코드5'")
	private String braDc5;

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '카드구분코드5'")
	private String cdDc5;

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '카드등급코드5'")
	private String cdGdc5;

	@Builder.Default
	@Column(columnDefinition = "varchar(2)  DEFAULT '02' COMMENT   '결제방법코드'")
	private String sttMdc = "02";

	@Column(columnDefinition = "varchar(3)  DEFAULT '' COMMENT   '법인규모상세코드'")
	private String ftbc;

	@Column(columnDefinition = "varchar(65)  DEFAULT '' COMMENT   '계좌번호'")
	private String acno;

	@Builder.Default
	@Column(columnDefinition = "varchar(2)  DEFAULT '06' COMMENT   '예금주관계코드'")
	private String dpOwRelc = "06";

	@Column(columnDefinition = "varchar(96)  DEFAULT '' COMMENT   '예금주명'")
	private String dpwnm;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '예금주주민번호'")
	private String dpOwRrno;

	@Builder.Default
	@Column(columnDefinition = "varchar(2)  DEFAULT '15' COMMENT   '결제일'")
	private String sttD = "15";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT '3' COMMENT   '결제일구분코드'")
	private String sttDDc = "3";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT 'Y' COMMENT   '법인책임계약여부'")
	private String sllConYn = "Y";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT 'N' COMMENT   '개인책임계약여부'")
	private String ilConYn = "N";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT 'N' COMMENT   '연대책임계약여부'")
	private String jlConYn = "N";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT '4' COMMENT   '청구서배달구분코드'")
	private String bllDrvyDc = "4";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT '1' COMMENT   '취소매출입금반영방식코드'")
	private String caslPayBMdc = "1";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT '1' COMMENT   '발송방법코드'")
	private String fwMdc = "1";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT 'N' COMMENT   '산출한도초과여부'")
	private String cmptLmovYn = "N";

	@Column(columnDefinition = "varchar(6)  DEFAULT '' COMMENT   '사업장우편번호'")
	private String bzplcPsno;

	@Column(columnDefinition = "varchar(60)  DEFAULT '' COMMENT   '사업장우편번호주소'")
	private String bzplcPnadd;

	@Column(columnDefinition = "varchar(150)  DEFAULT '' COMMENT   '사업장우편번호외주소'")
	private String bzplcBpnoAdd;

	@Column(columnDefinition = "varchar(4)  DEFAULT '' COMMENT   '사업장전화지역번호'")
	private String bzplcDdd;

	@Column(columnDefinition = "varchar(4)  DEFAULT '' COMMENT   '사업장전화국번'")
	private String bzplcExno;

	@Column(columnDefinition = "varchar(4)  DEFAULT '' COMMENT   '사업장전화개별번호'")
	private String bzplcTlno;

	@Column(columnDefinition = "varchar(7)  DEFAULT '' COMMENT   '법인사요청한도금액(만원)'")
	private String cpAkLimAm;

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT '2' COMMENT   '법인카드수령자구분코드'")
	private String cocTkpDc = "2";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT '0' COMMENT   '법인우선입금대상코드'")
	private String cpPdeOjC = "0";

	@Builder.Default
	@Column(columnDefinition = "varchar(3)  DEFAULT '001' COMMENT   '카드수령지구분코드'")
	private String addc = "001";

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '이동사업자번호'")
	private String mbzNo;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '이동전화국번'")
	private String mexno;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '이동전화개별번호'")
	private String mtlno;

	@Builder.Default
	@Column(columnDefinition = "varchar(5)  DEFAULT '20901' COMMENT   '유치자부서코드'")
	private String atrOgc = "20901";

	@Builder.Default
	@Column(columnDefinition = "varchar(7)  DEFAULT 'CS84047' COMMENT   '유치자사원번호'")
	private String atrEno = "CS84047";

	@Builder.Default
	@Column(columnDefinition = "varchar(5)  DEFAULT '20901' COMMENT   '당사담당자부서코드'")
	private String tcoCgpOgc = "20901";

	@Builder.Default
	@Column(columnDefinition = "varchar(7)  DEFAULT 'CS84047' COMMENT   '당사담당자사원번호'")
	private String tcoCgpEno = "CS84047";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT '1' COMMENT   '청구서언어구분코드'")
	private String bllLangDc = "1";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT '2' COMMENT   '법인청구방법코드'")
	private String cpBilMdc = "2";

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '청구서수령지구분코드'")
	private String bllRvpDc;

	@Column(columnDefinition = "varchar(128)  DEFAULT '' COMMENT   '메일ID'")
	private String mlId;

	@Column(columnDefinition = "varchar(40)  DEFAULT '' COMMENT   '법인조직명'")
	private String cpOgNm;

	@Column(columnDefinition = "varchar(20)  DEFAULT '' COMMENT   '법인조직영문명'")
	private String cpOgEnm;

	@Builder.Default
	@Column(columnDefinition = "varchar(2)  DEFAULT 'KR' COMMENT   '본사국적코드'")
	private String mnbrNatyC = "KR";

	@Builder.Default
	@Column(columnDefinition = "varchar(3)  DEFAULT '001' COMMENT   '청구지고객주소구분코드'")
	private String bilplcCaddc = "001";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT 'N' COMMENT   '즉발여부'")
	private String drisYn = "N";

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   'SMS서비스구분코드'")
	private String smsSvDc;

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT 'N' COMMENT   '요청한도법인월한도여부'")
	private String akLimCpMlimYn = "N";

	@Builder.Default
	@Column(columnDefinition = "varchar(40)  DEFAULT 'YYYYYYYYYYYYYY' COMMENT   '개인신용정보동의내용'")
	private String psnlCrifAgCn = "YYYYYYYYYYYYYY";

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '직장신주소여부'")
	private String offiNaddYn;

	@Column(columnDefinition = "varchar(100)  DEFAULT '' COMMENT   '직장신규우편번호외주소'")
	private String offiNewBpsnoAdd;

	@Column(columnDefinition = "varchar(25)  DEFAULT '' COMMENT   '직장건물관리번호'")
	private String offiBldMgno;

	@Column(columnDefinition = "varchar(96)  DEFAULT '' COMMENT   '수령자명'")
	private String tkpNm;

	@Column(columnDefinition = "varchar(64)  DEFAULT '' COMMENT   '수령자영문명'")
	private String tkpEnm;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '수령자주민번호'")
	private String tkpRrno;

	@Builder.Default
	@Column(columnDefinition = "varchar(20)  DEFAULT '대표이사' COMMENT   '수령자부서명'")
	private String tkpDpnm = "대표이사";

	@Builder.Default
	@Column(columnDefinition = "varchar(20)  DEFAULT '대표이사' COMMENT   '수령자직위명'")
	private String tkpPsiNm = "대표이사";

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '수령자국적코드'")
	private String tkpNatyC;

	@Column(columnDefinition = "varchar(128)  DEFAULT '' COMMENT   '수령자메일ID'")
	private String tkpMlId;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '수령자전화지역번호'")
	private String tkpDdd;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '수령자전화국번'")
	private String tkpExno;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '수령자전화개별번호'")
	private String tkpTlno;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '수령자이동사업자번호'")
	private String tkpMbzNo;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '수령자이동전화국번'")
	private String tkpMexno;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '수령자이동전화개별번호'")
	private String tkpMtlno;

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT '1' COMMENT   '실제소유자구분코드'")
	private String rlOwrDc = "1";

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '실제소유자구분상세코드'")
	private String rlOwrDdc;

	@Column(columnDefinition = "varchar(96)  DEFAULT '' COMMENT   '실제소유자명'")
	private String rlOwrNm;

	@Column(columnDefinition = "varchar(64)  DEFAULT '' COMMENT   '실제소유자영문명'")
	private String rlOwrEnm;

	@Column(columnDefinition = "varchar(32)  DEFAULT '' COMMENT   '실제소유자생년월일'")
	private String bird;

	@Column(columnDefinition = "varchar(2)  DEFAULT '' COMMENT   '실제소유자국적'")
	private String rlOwrNatyC;

	@Column(columnDefinition = "varchar(3)  DEFAULT '' COMMENT   '주주지분율'")
	private String stchShrR;

	@Column(columnDefinition = "varchar(100)  DEFAULT '' COMMENT   '심사진행메모내용(신청내용)'")
	private String insPrgMoCn;

	@Column(columnDefinition = "varchar(3)  DEFAULT '' COMMENT   '등록요청건수'")
	private String rgAkCt;

	@Column(columnDefinition = "varchar(3)  DEFAULT '' COMMENT   '등록요청건수2'")
	private String rgAkCt2;

	@Column(columnDefinition = "varchar(3)  DEFAULT '' COMMENT   '등록요청건수3'")
	private String rgAkCt3;

	@Column(columnDefinition = "varchar(3)  DEFAULT '' COMMENT   '등록요청건수4'")
	private String rgAkCt4;

	@Column(columnDefinition = "varchar(3)  DEFAULT '' COMMENT   '등록요청건수5'")
	private String rgAkCt5;

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT 'Y' COMMENT   '공인인증여부'")
	private String aosCtfYn = "Y";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT 'Y' COMMENT   '모바일인증여부'")
	private String mobCtfYn = "Y";

	@Builder.Default
	@Column(columnDefinition = "varchar(1)  DEFAULT 'Y' COMMENT   '카드신청가능여부'")
	private String cdaplPsyn = "Y";

	@Column(columnDefinition = "varchar(200)  DEFAULT '' COMMENT   '법인설립목적설명내용'")
	private String cpEstbPurpEplCn;

	@Column(columnDefinition = "varchar(7)  DEFAULT '' COMMENT   '법인카드요청한도금액(만원)'")
	private String akLimAm;

	@Column(columnDefinition = "varchar(6)  DEFAULT '' COMMENT   '관리자책임자우편번호'")
	private String tkpPsno;

	@Column(columnDefinition = "varchar(60)  DEFAULT '' COMMENT   '관리자책임자우편번호주소'")
	private String tkpPnadd;

	@Column(columnDefinition = "varchar(150)  DEFAULT '' COMMENT   '관리자책임자우편번호외주소'")
	private String tkpBpnoAdd;

	@Column(columnDefinition = "varchar(1)  DEFAULT '' COMMENT   '관리책임자신주소여부'")
	private String tkpNaddYn;

	@Column(columnDefinition = "varchar(100)  DEFAULT '' COMMENT   '관리책임자신규우편번호외주소'")
	private String tkpNewBpsnoAdd;

	@Column(columnDefinition = "varchar(25)  DEFAULT '' COMMENT   '관리책임자건물관리번호'")
	private String tkpBldMgno;

	@Column(columnDefinition = "varchar(34)  DEFAULT '' COMMENT   '신청카드비밀번호'")
	private String aplCdPswd;

	@Column(columnDefinition = "varchar(20)  DEFAULT '' COMMENT   '고객한글성'")
	private String cstHgFsn;

	@Column(columnDefinition = "varchar(20) DEFAULT '' COMMENT '고객한글이름'")
	private String cstHgLnm;

	@Column(columnDefinition = "varchar(20) DEFAULT '' COMMENT '고객영문성'")
	private String cstEngFsn;

	@Column(columnDefinition = "varchar(20) DEFAULT '' COMMENT '고객영문이름'")
	private String cstEngLnm;

	@Column(columnDefinition = "varchar(8) DEFAULT '' COMMENT '사업자등록증발급일자'")
	private String bzrgcIssd;

	@Column(columnDefinition = "varchar(2) DEFAULT '' COMMENT '상장증권거래소코드'")
	private String listStexC;

	@Column(columnDefinition = "varchar(1) DEFAULT '' COMMENT '가상유동취급업체여부'")
	private String vtCurTtEnpYn;

	@Column(columnDefinition = "varchar(2) DEFAULT '' COMMENT '설립목적코드'")
	private String estbPurpC;

	@Column(columnDefinition = "varchar(40) DEFAULT '' COMMENT '설립목적코드명'")
	private String estbPurpCNm;

	@Column(columnDefinition = "varchar(2) DEFAULT '' COMMENT '설립목적검증종류코드'")
	private String estbPurpVdtKndc;

	@Column(columnDefinition = "varchar(40) DEFAULT '' COMMENT '설립목적검증종류코드명'")
	private String estbPurpVdtKndcNm;

	@Builder.Default
	@Column(columnDefinition = "varchar(2) DEFAULT '03' COMMENT '거래목적코드'")
	private String dePurpC = "03";

	@Builder.Default
	@Column(columnDefinition = "varchar(60) DEFAULT '법인회원 개설' COMMENT '거래목적명'")
	private String dePurpNm = "법인회원개설";

	@Builder.Default
	@Column(columnDefinition = "varchar(2) DEFAULT '05' COMMENT '자금원천코드'")
	private String capOrgC = "05";

	@Builder.Default
	@Column(columnDefinition = "varchar(100) DEFAULT '법인운영자금' COMMENT '자금원천내용'")
	private String capOrgCn = "법인운영자금";

	@Builder.Default
	@Column(columnDefinition = "varchar(2) DEFAULT '02' COMMENT '법인단체구분코드'")
	private String cpGrupDc = "02";

	@Builder.Default
	@Column(columnDefinition = "varchar(40) DEFAULT '중소기업등' COMMENT '법인단체구분코드명'")
	private String cpGrupDcNm = "중소기업등";

	@Builder.Default
	@Column(columnDefinition = "varchar(1) DEFAULT 'Y' COMMENT '거주여부'")
	private String rceYn = "Y";

	@Column(columnDefinition = "varchar(2) DEFAULT '' COMMENT '본인확인경로코드'")
	private String hsVdPhc;

	@Column(columnDefinition = "varchar(40) DEFAULT '' COMMENT '신분증종류코드명'")
	private String idfKndcNm;

	@Column(columnDefinition = "varchar(50) DEFAULT '' COMMENT '신분증발급기관명'")
	private String idfIsuBurNm;

	@Column(columnDefinition = "varchar(64) DEFAULT '' COMMENT '신분증번호'")
	private String idfNo2;

	@Builder.Default
	@Column(columnDefinition = "varchar(2) DEFAULT '01' COMMENT '신분증검증구분코드'")
	private String idfVdtDc = "01";

	@Builder.Default
	@Column(columnDefinition = "varchar(40) DEFAULT '스크래핑' COMMENT '신분증검증구분코드명'")
	private String idfVdtDcNm = "스크래핑";

	@Builder.Default
	@Column(columnDefinition = "varchar(1) DEFAULT 'Y' COMMENT '신분증확인여부'")
	private String idfTCyn = "Y";

	@Builder.Default
	@Column(columnDefinition = "varchar(2) DEFAULT '03' COMMENT '거래목적코드'")
	private String tkpDePurpC = "03";

	@Builder.Default
	@Column(columnDefinition = "varchar(60) DEFAULT '법인회원 개설' COMMENT '거래목적명'")
	private String tkpDePurpNm = "법인회원 개설";

	@Builder.Default
	@Column(columnDefinition = "varchar(2) DEFAULT '05' COMMENT '자금원천코드'")
	private String tkpCapOrgC = "05";

	@Builder.Default
	@Column(columnDefinition = "varchar(100) DEFAULT '법인운영자금' COMMENT '자금원천내용'")
	private String tkpCapOrgCn = "법인운영자금";

	@Builder.Default
	@Column(columnDefinition = "varchar(2) DEFAULT '11' COMMENT 'AML직업구분코드'")
	private String amlJobDc = "11";

	@Builder.Default
	@Column(columnDefinition = "varchar(6) DEFAULT '110005' COMMENT 'AML산업분류코드'")
	private String amlIdsClac = "110005";

	@Column(columnDefinition = "varchar(1) DEFAULT '' COMMENT '남녀구분코드'")
	private String maFemDc;

	@Builder.Default
	@Column(columnDefinition = "varchar(1) DEFAULT 'N' COMMENT '실제소유자확인생략대상여부'")
	private String rlOwrVdOmtOjYn = "N";

	@Column(columnDefinition = "varchar(1) DEFAULT '' COMMENT '실제소유자확인생략대상코드'")
	private String rlOwrVdOmtOjC;

	@Builder.Default
	@Column(columnDefinition = "varchar(2) DEFAULT '01' COMMENT '실제소유자확인방법코드'")
	private String rlOwrVdMdc = "01";

	@Builder.Default
	@Column(columnDefinition = "varchar(1) DEFAULT '9' COMMENT '실제소유자남녀구분코드'")
	private String rlMaFemDc = "9";

	@Column(columnDefinition = "varchar(1) DEFAULT '' COMMENT '고위드 기업 등급'")
	private String gowidEtrGdV;

	@Column(columnDefinition = "varchar(1) DEFAULT '' COMMENT '벤처확인서보유여부'")
	private String vtbCfHvYn;

	@Column(columnDefinition = "varchar(1) DEFAULT '' COMMENT 'VC투자유치여부'")
	private String ivArYn;

	@Column(columnDefinition = "varchar(7) DEFAULT '' COMMENT '고위드계산한도 (만원)'")
	private String gowidCalLimAm;

	@Builder.Default
	@Column(columnDefinition = "varchar(15) DEFAULT '0' COMMENT '기준잔고 (원)'")
	private String gowidCriBalAm = "0";

	@Builder.Default
	@Column(columnDefinition = "varchar(15) DEFAULT '0' COMMENT '45일평균잔고 (원)'")
	private String gowid45DAvBalAm = "0";

	@Builder.Default
	@Column(columnDefinition = "varchar(15) DEFAULT '0' COMMENT '45일중간잔고 (원)'")
	private String gowid45DMidBalAm = "0";

	@Builder.Default
	@Column(columnDefinition = "varchar(15) DEFAULT '0' COMMENT '현재잔고 (원)'")
	private String gowidPsBalAm = "0";

	@Column(columnDefinition = "varchar(1) DEFAULT '' COMMENT '접수 완료 여부'")
	private String rcpEndYn;

	@Column(columnDefinition = "varchar(100) DEFAULT '' COMMENT '접수메시지'")
	private String rcpMsg;

	@Column(columnDefinition = "varchar(14) DEFAULT '' COMMENT '접수일련번호'")
	private String apfRcpno;
}