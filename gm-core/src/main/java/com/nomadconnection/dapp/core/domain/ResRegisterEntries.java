package com.nomadconnection.dapp.core.domain;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResRegisterEntries extends BaseTime {

	// 법인등기부등본

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long idx;

	private String resDocTitle;  //문서제목
	private String resRegistrationNumber;  //등기번호
	private String resRegNumber;  //등록번호
	private String competentRegistryOffice;  //관할등기소
	private String resPublishRegistryOffice;  //발행등기소
	private String resPublishDate;  //발행일자
	private String companyNm;  //상호
	private String companyNmChDate;  //상호_변경일자
	private String companyNmRegDate;  //상호_등기일자
	private String headOffAddress;  //본점주소
	private String headOffAddressChDate;  //본점주소_변경일자
	private String headOffAddressRegDate;  //본점주소_등기일자
	private String weekAmount;  //1주의금액
	private String weekAmountChDate;  //1주의금액_변경일자
	private String amountPerWeekDateOfReg;  //1주의금액_등기일자
	private String stockTotal;  //발행할주식의총수
	private String stockTotalChDate;  //발행할주식의총수_변경일자
	private String stockTotalRegDate;  //발행할주식의총수_등기일자
	private String stocksTotal;  //발행주식현황_총수
	private String stocksType1;  //발행주식현황_종류1
	private String stockType1Qua;  //발행주식현황_종류1_수량
	private String stocksType2;  //발행주식현황_종류2
	private String stockType2Qua;  //발행주식현황_종류2_수량
	private String stocksType3;  //발행주식현황_종류3
	private String stockType3Qua;  //발행주식현황_종류3_수량
	private String stocksType4;  //발행주식현황_종류4
	private String stocksType4Qua;  //발행주식현황_종류4_수량
	private String stocksType5;  //발행주식현황_종류5
	private String stockType5Qua;  //발행주식현황_종류5_수량
	private String stocksType6;  //발행주식현황_종류6
	private String stocksType6Qua;  //발행주식현황_종류6_수량
	private String stocksType7;  //발행주식현황_종류7
	private String stockType7Qua;  //발행주식현황_종류7_수량
	private String stocksType8;  //발행주식현황_종류8
	private String stockType8Qua;  //발행주식현황_종류8_수량
	private String stocksType9;  //발행주식현황_종류9
	private String stockType9Qua;  //발행주식현황_종류9_수량
	private String stocksType10;  //발행주식현황_종류10
	private String stockType10Qua;  //발행주식현황_종류10_수량
	private String sharesCapitalAmount;  //발행주식현황_자본금의액
	private String sharesChDate;  //발행주식현황_변경일자
	private String stockStatusRegDate;  //발행주식현황_등기일자
	private String ceoSpot1;  //대표이사_직위1
	private String ceoSpotNm1;  //대표이사_성명1
	private String ceoSpotResRNum1;  //대표이사_주민번호1
	private String ceoAddress1;  //대표이사_주소1
	private String ceoSpot2;  //대표이사_직위2
	private String ceoSpotNm2;  //대표이사_성명2
	private String ceoSpotResRNum2;  //대표이사_주민번호2
	private String ceoAddress2;  //대표이사_주소2
	private String ceoSpot3;  //대표이사_직위3
	private String ceoSpotNm3;  //대표이사_성명3
	private String ceoSpotResRNum3;  //대표이사_주민번호3
	private String ceoAddress3;  //대표이사_주소3
	private String corpCreatedDate;  //법인성립연월일
	private String privateationDate;  //발행일자
}
