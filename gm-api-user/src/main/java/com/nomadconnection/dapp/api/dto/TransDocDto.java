package com.nomadconnection.dapp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class TransDocDto {

	// 재무제표
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class resFincStateDto {
		public String resCompanyIdentityNo; //사업자등록번호
		public String resIssueNo; //발급(승인)번호
		public String resUserIdentiyNo; //주민번호
		public String resCompanyNm; //상호(사업장명)
		public String resIssueFlag; //발급가능여부
		public String commStartDate; //시작일자
		public String commEndDate; //종료일자
		public String resUserNm; //성명
		public String resUserAddr; //주소
		public String resBusinessItems; //종목
		public String resBusinessTypes; //업태
		public String resReportingDate; //작성일자
		public String resAttrYear; //귀속연도
		public String resBalanceSheet; //총자산
		public String resIncomeStatement; //매출
		public String resBalanceSheet_amt; //납입자본금
		public String resBalanceSheet_total; //자기자본금
		public String financialPeriod; //재무조사일
	}

	// 법인등기부등본
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class resRegisterEntriesDto {
		public String resDocTitle;  //문서제목
		public String resRegistrationNumber;  //등기번호
		public String resRegNumber;  //등록번호
		public String competentRegistryOffice;  //관할등기소
		public String resPublishRegistryOffice;  //발행등기소
		public String resPublishDate;  //발행일자
		public String companyNm;  //상호
		public String companyNmChDate;  //상호_변경일자
		public String companyNmRegDate;  //상호_등기일자
		public String headOffAddress;  //본점주소
		public String headOffAddressChDate;  //본점주소_변경일자
		public String headOffAddressRegDate;  //본점주소_등기일자
		public String weekAmount;  //1주의금액
		public String weekAmountChDate;  //1주의금액_변경일자
		public String amountPerWeekDateOfReg;  //1주의금액_등기일자
		public String stockTotal;  //발행할주식의총수
		public String stockTotalChDate;  //발행할주식의총수_변경일자
		public String stockTotalRegDate;  //발행할주식의총수_등기일자
		public String stocksTotal;  //발행주식현황_총수
		public String stocksType1;  //발행주식현황_종류1
		public String stockType1Qua;  //발행주식현황_종류1_수량
		public String stocksType2;  //발행주식현황_종류2
		public String stockType2Qua;  //발행주식현황_종류2_수량
		public String stocksType3;  //발행주식현황_종류3
		public String stockType3Qua;  //발행주식현황_종류3_수량
		public String stocksType4;  //발행주식현황_종류4
		public String stocksType4Qua;  //발행주식현황_종류4_수량
		public String stocksType5;  //발행주식현황_종류5
		public String stockType5Qua;  //발행주식현황_종류5_수량
		public String stocksType6;  //발행주식현황_종류6
		public String stocksType6Qua;  //발행주식현황_종류6_수량
		public String stocksType7;  //발행주식현황_종류7
		public String stockType7Qua;  //발행주식현황_종류7_수량
		public String stocksType8;  //발행주식현황_종류8
		public String stockType8Qua;  //발행주식현황_종류8_수량
		public String stocksType9;  //발행주식현황_종류9
		public String stockType9Qua;  //발행주식현황_종류9_수량
		public String stocksType10;  //발행주식현황_종류10
		public String stockType10Qua;  //발행주식현황_종류10_수량
		public String sharesCapitalAmount;  //발행주식현황_자본금의액
		public String sharesChDate;  //발행주식현황_변경일자
		public String stockStatusRegDate;  //발행주식현황_등기일자
		public String ceoSpot1;  //대표이사_직위1
		public String ceoSpotNm1;  //대표이사_성명1
		public String ceoSpotResRNum1;  //대표이사_주민번호1
		public String ceoAddress1;  //대표이사_주소1
		public String ceoSpot2;  //대표이사_직위2
		public String ceoSpotNm2;  //대표이사_성명2
		public String ceoSpotResRNum2;  //대표이사_주민번호2
		public String ceoAddress2;  //대표이사_주소2
		public String ceoSpot3;  //대표이사_직위3
		public String ceoSpotNm3;  //대표이사_성명3
		public String ceoSpotResRNum3;  //대표이사_주민번호3
		public String ceoAddress3;  //대표이사_주소3
		public String corpCreatedDate;  //법인성립연월일
		public String publicationDate;  //발행일자
	}
}
