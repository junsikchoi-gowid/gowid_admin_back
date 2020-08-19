package com.nomadconnection.dapp.api.dto.lotte;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @interfaceID : 1100
 * @description : 법인카드신청
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart1100 extends CommonPart {

	private String arPhc; // 유치경로코드

	private String cdaplDc; // 카드신청구분코드

	private String dgTc; // 대표자유형

	private String dgRrno; // 대표자주민번호

	private String cstNm; // 고객명'

	private String cstEnm; // 고객영문명

	private String natyC; // 국적코드

	private String dgRrno2; // 대표자주민번호2

	private String cstNm2; // 고객명2

	private String cstEnm2; // 고객영문명2

	private String natyC2; // 국적코드2

	private String maFemDc2; // 남녀구분코드2

	private String dgRrno3; // 대표자주민번호3

	private String cstNm3; // 고객명3

	private String cstEnm3; // 고객영문명3

	private String natyC3; // 국적코드3

	private String maFemDc3; // 남녀구분코드3

	private String bzno; // 사업자번호

	private String cpNo; // 법인번호

	private String estbDt; // 설립일자

	private String cpBtc; // 법인업종코드

	private String cpTc; // 법인유형코드

	private String cpSclC; // 법인규모코드

	private String cpSclDtc; // 법인규모상세코드

	private String cocPdDc; // 법인카드상품구분코드

	private String unitCdC; // 카드상품종류코드

	private String braDc; // 로고구분코드

	private String cdDc; // 카드구분코드

	private String cdGdc; // 카드등급코드

	private String cocPdDc2;

	private String unitCdC2;

	private String braDc2;

	private String cdDc2;

	private String cdGdc2;

	private String cocPdDc3;

	private String unitCdC3;

	private String braDc3;

	private String cdDc3;

	private String cdGdc3;

	private String cocPdDc4;

	private String unitCdC4;

	private String braDc4;

	private String cdDc4;

	private String cdGdc4;

	private String cocPdDc5;

	private String unitCdC5;

	private String braDc5;

	private String cdDc5;

	private String cdGdc5;

	private String sttMdc; // 결제방법코드

	private String ftbc; // 법인규모상세코드

	private String acno; // 계좌번호

	private String dpOwRelc; // 예금주관계코드

	private String dpwnm; // 예금주명

	private String dpOwRrno; // 예금주주민번호

	private String sttD; // 결제일

	private String sttDDc; // 결제일구분코드

	private String sllConYn; // 법인책임계약여부

	private String ilConYn; // 개인책임계약여부

	private String jlConYn; // 연대책임계약여부

	private String bllDrvyDc; // 청구서배달구분코드

	private String caslPayBMdc; // 취소매출입금반영방식코드

	private String fwMdc; // 발송방법코드

	private String cmptLmovYn; // 산출한도초과여부

	private String bzplcPsno; // 사업장우편번호

	private String bzplcPnadd; // 사업장우편번호주소

	private String bzplcBpnoAdd; // 사업장우편번호외주소

	private String bzplcDdd; // 사업장전화지역번호

	private String bzplcExno; // 사업장전화국번

	private String bzplcTlno; // 사업장전화개별번호

	private String cpAkLimAm; // 법인사요청한도금액(만원)

	private String cocTkpDc; // 법인카드수령자구분코드

	private String cpPdeOjC; // 법인우선입금대상코드

	private String addc; // 카드수령지구분코드

	private String mbzNo; // 이동사업자번호

	private String mexno; // 이동전화국번

	private String mtlno; // 이동전화개별번호

	private String atrOgc; // 유치자부서코드

	private String atrEno; // 유치자사원번호

	private String tcoCgpOgc; // 당사담당자부서코드

	private String tcoCgpEno; // 당사담당자사원번호

	private String bllLangDc; // 청구서언어구분코드

	private String cpBilMdc; // 법인청구방법코드

	private String bllRvpDc; // 청구서수령지구분코드

	private String mlId; // 메일ID

	private String cpOgNm; // 법인조직명

	private String cpOgEnm; // 법인조직영문명

	private String mnbrNatyC; // 본사국적코드

	private String bilplcCaddc; // 청구지고객주소구분코드

	private String drisYn; // 즉발여부

	private String smsSvDc; // SMS서비스구분코드

	private String akLimCpMlimYn; // 요청한도법인월한도여부

	private String psnlCrifAgCn; // 개인신용정보동의내용

	private String offiNaddYn; // 직장신주소여부

	private String offiNewBpsnoAdd; // 직장신규우편번호외주소

	private String offiBldMgno; // 직장건물관리번호

	private String tkpNm; // 수령자명

	private String tkpEnm; // 수령자영문명

	private String tkpRrno; // 수령자주민번호

	private String tkpDpnm; // 수령자부서명

	private String tkpPsiNm; // 수령자직위명

	private String tkpNatyC; // 수령자국적코드

	private String tkpMlId; // 수령자메일ID

	private String tkpDdd; // 수령자전화지역번호

	private String tkpExno; // 수령자전화국번

	private String tkpTlno; // 수령자전화개별번호

	private String tkpMbzNo; // 수령자이동사업자번호

	private String tkpMexno; // 수령자이동전화국번

	private String tkpMtlno; // 수령자이동전화개별번호

	private String rlOwrDc; // 실제소유자구분코드

	private String rlOwrDdc; // 실제소유자구분상세코드

	private String rlOwrNm; // 실제소유자명

	private String rlOwrEnm; // 실제소유자영문명

	private String bird; // 실제소유자생년월일

	private String rlOwrNatyC; // 실제소유자국적

	private String stchShrR; // 주주지분율

	private String insPrgMoCn; // 심사진행메모내용(신청내용)

	private String rgAkCt; // 등록요청건수

	private String rgAkCt2; // 등록요청건수2

	private String rgAkCt3; // 등록요청건수3

	private String rgAkCt4; // 등록요청건수4

	private String rgAkCt5; // 등록요청건수5

	private String aosCtfYn; // 공인인증여부

	private String mobCtfYn; // 모바일인증여부

	private String cdaplPsyn; // 카드신청가능여부

	private String cpEstbPurpEplCn; // 법인설립목적설명내용

	private String akLimAm; // 법인카드요청한도금액(만원)

	private String tkpPsno; // 관리자책임자우편번호

	private String tkpPnadd; // 관리자책임자우편번호주소

	private String tkpBpnoAdd; // 관리자책임자우편번호외주소

	private String tkpNaddYn; // 관리책임자신주소여부

	private String tkpNewBpsnoAdd; // 관리책임자신규우편번호외주소

	private String tkpBldMgno; // 관리책임자건물관리번호

	private String aplCdPswd; // 신청카드비밀번호

	private String cstHgFsn; // 고객한글성

	private String cstHgLnm; // 고객한글이름

	private String cstEngFsn; // 고객영문성

	private String cstEngLnm; // 고객영문이름

	private String bzrgcIssd; // 사업자등록증발급일자

	private String listStexC; // 상장증권거래소코드

	private String vtCurTtEnpYn; // 가상유동취급업체여부

	private String estbPurpC; // 설립목적코드

	private String estbPurpCNm; // 설립목적코드명

	private String estbPurpVdtKndc; // 설립목적검증종류코드

	private String estbPurpVdtKndcNm; // 설립목적검증종류코드명

	private String dePurpC; // 거래목적코드

	private String dePurpNm; // 거래목적명

	private String capOrgC; // 자금원천코드

	private String capOrgCn; // 자금원천내용

	private String cpGrupDc; // 법인단체구분코드

	private String cpGrupDcNm; // 법인단체구분코드명

	private String rceYn; // 거주여부

	private String hsVdPhc; // 본인확인경로코드

	private String idfKndcNm; // 신분증종류코드명

	private String idfIsuBurNm; // 신분증발급기관명

	private String idfNo2; // 신분증번호

	private String idfVdtDc; // 신분증검증구분코드

	private String idfVdtDcNm; // 신분증검증구분코드명

	private String idfTCyn; // 신분증확인여부

	private String tkpDePurpC; // 거래목적코드

	private String tkpDePurpNm; // 거래목적명

	private String tkpCapOrgC; // 자금원천코드

	private String tkpCapOrgCn; // 자금원천내용

	private String amlJobDc; // AML직업구분코드

	private String amlIdsClac; // AML산업분류코드

	private String maFemDc; // 남녀구분코드

	private String rlOwrVdOmtOjYn; // 실제소유자확인생략대상여부

	private String rlOwrVdOmtOjC; // 실제소유자확인생략대상코드

	private String rlOwrVdMdc; // 실제소유자확인방법코드

	private String rlMaFemDc; // 실제소유자남녀구분코드

	private String gowidEtrGdV; // 고위드 기업 등급

	private String vtbCfHvYn; // 벤처확인서보유여부

	private String ivArYn; // VC투자유치여부

	private String gowidCalLimAm; // 고위드계산한도 (만원)

	private String gowidCriBalAm; // 기준잔고 (원)

	private String gowid45DAvBalAm; // 45일평균잔고 (원)

	private String gowid45DMidBalAm; // 45일중간잔고 (원)

	private String gowidPsBalAm; // 현재잔고 (원)

	private String rcpEndYn; // 접수 완료 여부

	private String rcpMsg; // 접수메시지

	private String apfRcpno; // 접수일련번호
}
