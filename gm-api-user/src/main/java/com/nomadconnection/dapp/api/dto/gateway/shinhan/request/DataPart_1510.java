package com.nomadconnection.dapp.api.dto.gateway.shinhan.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

/**
 * @interfaceID : 1510
 * @description : 사업자등록증스크래핑
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class DataPart_1510 extends CommonPart {

    @JsonAlias("D001")
    private String issueNo; // 발급번호

    @JsonAlias("D002")
    private String companyName; // 법인명(상호)

    @JsonAlias("D003")
    private String businessLicenseNo;    // 사업자등록번호

    @JsonAlias("D004")
    private String companyType; // 사업자종류

    @JsonAlias("D005")
    private String ceoName; // 성명(대표자)

    @JsonAlias("D006")
    private String address; //사업장소재지(주소)

    @JsonAlias("D007")
    private String registerNo;  // 주민등록번호(법인등록번호)

    @JsonAlias("D008")
    private String openDate;    // 개업일

    @JsonAlias("D009")
    private String registerDate; // 사업자등록일

    @JsonAlias("D010")
    private String issuingAgency; // 발급기관

    @JsonAlias("D011")
    private String industry;    // 업태

    @JsonAlias("D011")
    private String category;    // 종목

}
