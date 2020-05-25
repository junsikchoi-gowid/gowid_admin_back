package com.nomadconnection.dapp.api.dto.gateway.shinhan.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

/**
 * @interfaceID : 1520
 * @description : 재무제표스크래핑
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class DataPart_1520 extends CommonPart {

    @JsonAlias("D001")
    private String businessLicenseNo;    // 사업자등록번호

    @JsonAlias("D002")
    private String approvalNo;    // 발급(승인)번호

    @JsonAlias("D003")
    private String registerNo;  // 주민번호

    @JsonAlias("D004")
    private String companyName; // 상호(사업장명)

    @JsonAlias("D005")
    private String availableIssueYn;    // 발급가능여부

    @JsonAlias("D006")
    private String startDate;   // 시작일자

    @JsonAlias("D007")
    private String endDate; // 종료일자

    @JsonAlias("D008")
    private String name;    // 성명

    @JsonAlias("D009")
    private String address; // 주소

    @JsonAlias("D010")
    private String category;    // 종목

    @JsonAlias("D011")
    private String industry;    // 업태

    @JsonAlias("D012")
    private String createDate;  // 작성일자

    @JsonAlias("D013")
    private String yearOfAttribution;   // 귀속연도

    @JsonAlias("D014")
    private String totalAsset;  // 총자산

    @JsonAlias("D015")
    private String sales;   // 매출

    @JsonAlias("D016")
    private String paidCapital;   // 납입자본금

    @JsonAlias("D017")
    private String equityCapital;   // 자기자본금

    @JsonAlias("D018")
    private String financialSurveyDate; // 재무조사일

}
