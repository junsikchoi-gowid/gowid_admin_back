package com.nomadconnection.dapp.api.dto.gateway.shinhan.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

/**
 * @interfaceID : 1530
 * @description : 등기부등본스크래핑
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class DataPart_1530 extends CommonPart {

    @JsonAlias("D001")
    private String docTitle;    // 문서제목

    @JsonAlias("D002")
    private String registrationNo;  // 등기번호

    @JsonAlias("D003")
    private String registerNo;  // 등록번호

    @JsonAlias("D004")
    private String controlRegistryOffice;   // 관할등기소

    @JsonAlias("D005")
    private String publishRegistryOffice;   // 발행등기소

    @JsonAlias("D006")
    private String issueDate;   // 발행일자

    @JsonAlias("D007")
    private String companyName; // 상호

    @JsonAlias("D008")
    private String nameChangeDate;   // 상호_변경일자

    @JsonAlias("D009")
    private String nameRegistrationDate;    // 상호_등기일자

    @JsonAlias("D010")
    private String headOfficeAddress;   // 본점주소

    @JsonAlias("D011")
    private String addressChangeDate; // 본점주소_변경일자

    @JsonAlias("D012")
    private String addressRegistrationDate;   // 본점주소_등기일자

    @JsonAlias("D013")
    private String amountOfStock;   // 1주의금액

    @JsonAlias("D014")
    private String amountOfStockChangeDate; // 1주의금액_변경일자

    @JsonAlias("D015")
    private String amountOfStockRegistrationDate; // 1주의금액_등기일자

    @JsonAlias("D016")
    private String totalNumOfToBeStocks; // 발행할주식의총수

    @JsonAlias("D017")
    private String totalNumOfToBeStocksChangeDate;   // 발행할주식의총수_변경일자

    @JsonAlias("D018")
    private String totalNumOfToBeStocksRegistrationDate;   // 발행할주식의총수_등기일자

    @JsonAlias("D019")
    private String totalNumOfStocks;    // 발행주식현황_총수

    @JsonAlias("D020")
    private String kindOfStock1;    // 발행주식현황_종류1

    @JsonAlias("D021")
    private String amountKindOfStock1;  // 발행주식현황_종류1_수량

    @JsonAlias("D022")
    private String kindOfStock2;    // 발행주식현황_종류2

    @JsonAlias("D023")
    private String amountKindOfStock2;  // 발행주식현황_종류2_수량

    @JsonAlias("D024")
    private String kindOfStock3;    // 발행주식현황_종류3

    @JsonAlias("D025")
    private String amountKindOfStock3;  // 발행주식현황_종류3_수량

    @JsonAlias("D026")
    private String kindOfStock4;    // 발행주식현황_종류4

    @JsonAlias("D027")
    private String amountKindOfStock4;  // 발행주식현황_종류4_수량

    @JsonAlias("D028")
    private String kindOfStock5;    // 발행주식현황_종류5

    @JsonAlias("D029")
    private String amountKindOfStock5;  // 발행주식현황_종류5_수량

    @JsonAlias("D030")
    private String kindOfStock6;    // 발행주식현황_종류6

    @JsonAlias("D031")
    private String amountKindOfStock6;  // 발행주식현황_종류6_수량

    @JsonAlias("D032")
    private String kindOfStock7;    // 발행주식현황_종류7

    @JsonAlias("D033")
    private String amountKindOfStock7;  // 발행주식현황_종류7_수량

    @JsonAlias("D034")
    private String kindOfStock8;    // 발행주식현황_종류8

    @JsonAlias("D035")
    private String amountKindOfStock8;  // 발행주식현황_종류8_수량

    @JsonAlias("D036")
    private String kindOfStock9;    // 발행주식현황_종류9

    @JsonAlias("D037")
    private String amountKindOfStock9;  // 발행주식현황_종류9_수량

    @JsonAlias("D038")
    private String kindOfStock10;    // 발행주식현황_종류10

    @JsonAlias("D039")
    private String amountKindOfStock10;  // 발행주식현황_종류10_수량


    @JsonAlias("D040")
    private String CapitalOfStock;  // 발행주식현황_자본금의액


    @JsonAlias("D041")
    private String StockChangeDate; // 발행주식현황_변경일자


    @JsonAlias("D042")
    private String StockRegistrationDate;   // 발행주식현황_등기일자

    @JsonAlias("D043")
    private String ceoPosition1;    // 대표이사_직위1

    @JsonAlias("D044")
    private String ceoName1;    // 대표이사_성명1

    @JsonAlias("D045")
    private String ceoRegisterNo1;  // 대표이사_주민번호1

    @JsonAlias("D046")
    private String ceoAddress1; // 대표이사_주소1

    @JsonAlias("D047")
    private String ceoPosition2;    // 대표이사_직위2

    @JsonAlias("D048")
    private String ceoName2;    // 대표이사_성명2

    @JsonAlias("D049")
    private String ceoRegisterNo2;  // 대표이사_주민번호2

    @JsonAlias("D050")
    private String ceoAddress2; // 대표이사_주소2

    @JsonAlias("D051")
    private String ceoPosition3;    // 대표이사_직위3

    @JsonAlias("D052")
    private String ceoName3;    // 대표이사_성명3

    @JsonAlias("D053")
    private String ceoRegisterNo3;  // 대표이사_주민번호3

    @JsonAlias("D054")
    private String ceoAddress3; // 대표이사_주소3

    @JsonAlias("D055")
    private String companyOpenDate; // 법인설립연월일

    @JsonAlias("D056")
    private String issueDate2;   // 발행일자


}
