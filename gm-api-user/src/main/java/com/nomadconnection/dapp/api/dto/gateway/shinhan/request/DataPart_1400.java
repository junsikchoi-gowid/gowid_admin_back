package com.nomadconnection.dapp.api.dto.gateway.shinhan.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

/**
 * @interfaceID : 1400
 * @description : 법인회원조건변경신청
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class DataPart_1400 extends CommonPart {

    @JsonAlias("D001")
    private String transType;   // 거래구분코드

    @JsonAlias("D002")
    private String businessLicenseNo;    // 사업자등록번호

    @JsonAlias("D003")
    private String memberTypeCode;  // 회원구분코드

    @JsonAlias("D004")
    private String companyName; // 법인명

    @JsonAlias("D005")
    private String scaleCode;   // 기업규모코드

    @JsonAlias("D006")
    private String ceoRegisterNo;   // 대표자주민등록번호

    @JsonAlias("D007")
    private String ceoName; // 대표자명

    @JsonAlias("D008")
    private String guarantorIdentifyNo; // 보증인고객식별번호

    @JsonAlias("D009")
    private String guarantorName;   // 보증인고객한글명

    @JsonAlias("D010")
    private Long currentLimitAmount;  // 현재제휴한도

    @JsonAlias("D011")
    private String industryCode;    // 업종코드

    @JsonAlias("D012")
    private String specialCardCode; // 특화카드구분코드

    @JsonAlias("D013")
    private String conditionApplyCode;  // 조건변경법인심사신청구분코드

    @JsonAlias("D014")
    private Long afterLimitAmount;    // 변경후제휴한도금액

    @JsonAlias("D015")
    private String applicantNo; // 신청인사번

    @JsonAlias("D016")
    private String verifierNo;  // 확인자사번

    @JsonAlias("D017")
    private String opinion; // 의견내용

    @JsonAlias("D018")
    private String registerBranchCode;  // 등록지점코드

    @JsonAlias("D019")
    private String ownerName;   // 법인실소유자한글명

    @JsonAlias("D020")
    private String ownerEngName;    // 법인실소유자영문명

    @JsonAlias("D021")
    private String ownerBirthDate;  // 법인실소유자생년월일

    @JsonAlias("D022")
    private String ownerNtnCode;    // 법인실소유자국적코드

    @JsonAlias("D023")
    private String ownerTypeCode;   // 법인실소유자유형코드

    @JsonAlias("D024")
    private String amlOwnerStake;   // 자금세탁방지실소유자지분율

}
